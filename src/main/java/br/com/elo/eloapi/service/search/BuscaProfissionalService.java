package br.com.elo.eloapi.service.search;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.exception.SearchUnavailableException;
import br.com.elo.eloapi.model.search.ProfissionalSearchDocument;
import br.com.elo.eloapi.model.search.dto.BuscaProfissionalFiltro;
import br.com.elo.eloapi.model.search.dto.BuscaProfissionalRS;
import br.com.elo.eloapi.model.search.dto.OrdenacaoBuscaProfissional;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static br.com.elo.eloapi.Util.Utils.calculaDistancia;

@Service
@RequiredArgsConstructor
public class BuscaProfissionalService {

    private final ElasticsearchClient elasticsearchClient;
    private final ProfissionalSearchIndexManager indexManager;

    public BuscaProfissionalRS buscar(BuscaProfissionalFiltro filtro) {
        validar(filtro);
        try {
            indexManager.garantirIndice();
            SearchRequest request = criarRequest(filtro);
            SearchResponse<ProfissionalSearchDocument> response = elasticsearchClient.search(request, ProfissionalSearchDocument.class);

            List<BuscaProfissionalRS.ProfissionalBuscaRS> profissionais = response.hits().hits().stream()
                    .map(hit -> toResponse(hit.source(), filtro))
                    .filter(Objects::nonNull)
                    .toList();

            long total = response.hits().total() == null ? profissionais.size() : response.hits().total().value();
            return new BuscaProfissionalRS(profissionais, total, filtro.pagina(), filtro.tamanho());
        } catch (IOException exception) {
            throw new SearchUnavailableException("O servico de busca esta temporariamente indisponivel.", exception);
        }
    }

    private SearchRequest criarRequest(BuscaProfissionalFiltro filtro) {
        SearchRequest.Builder request = new SearchRequest.Builder()
                .index(indexManager.getIndexName())
                .from(filtro.pagina() * filtro.tamanho())
                .size(filtro.tamanho())
                .trackTotalHits(total -> total.enabled(true))
                .query(criarQuery(filtro));

        adicionarOrdenacao(request, filtro);
        request.sort(sort -> sort.field(field -> field
                .field("profissionalId")
                .order(SortOrder.Asc)));
        return request.build();
    }

    private Query criarQuery(BuscaProfissionalFiltro filtro) {
        BoolQuery.Builder principal = new BoolQuery.Builder()
                .filter(query -> query.term(term -> term.field("habilitado").value(true)));

        BoolQuery.Builder filtroServicos = new BoolQuery.Builder()
                .filter(query -> query.term(term -> term.field("servicos.ativo").value(true)));

        if (filtro.categoriaId() != null) {
            filtroServicos.filter(query -> query.term(term -> term
                    .field("servicos.categoriaGeralId")
                    .value(filtro.categoriaId())));
        }

        boolean possuiTexto = StringUtils.hasText(filtro.texto());
        Query nestedFiltroServicos = Query.of(query -> query.nested(nested -> nested
                .path("servicos")
                .query(filtroServicos.build()._toQuery())));
        principal.filter(nestedFiltroServicos);

        if (possuiTexto) {
            BoolQuery.Builder buscaNosServicos = new BoolQuery.Builder()
                    .filter(query -> query.term(term -> term.field("servicos.ativo").value(true)))
                    .must(query -> query.multiMatch(multiMatch -> multiMatch
                            .query(filtro.texto().trim())
                            .fields(
                                    "servicos.categoriaGeral^5",
                                    "servicos.categoriaEspecifica^4",
                                    "servicos.tags^3",
                                    "servicos.descricao^2"
                            )
                            .fuzziness("AUTO")));
            if (filtro.categoriaId() != null) {
                buscaNosServicos.filter(query -> query.term(term -> term
                        .field("servicos.categoriaGeralId")
                        .value(filtro.categoriaId())));
            }

            Query nestedBuscaTextual = Query.of(query -> query.nested(nested -> nested
                    .path("servicos")
                    .query(buscaNosServicos.build()._toQuery())));
            principal.should(query -> query.match(match -> match
                    .field("nome")
                    .query(filtro.texto().trim())
                    .fuzziness("AUTO")
                    .boost(2.0f)));
            principal.should(nestedBuscaTextual);
            principal.minimumShouldMatch("1");
        }

        if (filtro.avaliacaoMinima() != null) {
            principal.filter(query -> query.range(range -> range.number(number -> number
                    .field("avaliacao")
                    .gte(filtro.avaliacaoMinima()))));
        }

        if (possuiCoordenadas(filtro)) {
            principal.filter(query -> query.geoDistance(geo -> geo
                    .field("localizacao")
                    .distance(String.format(Locale.ROOT, "%skm", filtro.distanciaKm()))
                    .location(location -> location.latlon(latLon -> latLon
                            .lat(filtro.latitude())
                            .lon(filtro.longitude())))));
        }

        return principal.build()._toQuery();
    }

    private void adicionarOrdenacao(SearchRequest.Builder request, BuscaProfissionalFiltro filtro) {
        OrdenacaoBuscaProfissional ordenacao = filtro.ordenacao();
        if (ordenacao == OrdenacaoBuscaProfissional.DISTANCIA) {
            adicionarOrdenacaoDistancia(request, filtro);
            adicionarOrdenacaoAvaliacao(request);
            return;
        }

        if (ordenacao == OrdenacaoBuscaProfissional.AVALIACAO) {
            adicionarOrdenacaoAvaliacao(request);
            return;
        }

        if (StringUtils.hasText(filtro.texto())) {
            request.sort(sort -> sort.score(score -> score.order(SortOrder.Desc)));
        }
        if (possuiCoordenadas(filtro)) {
            adicionarOrdenacaoDistancia(request, filtro);
        }
        adicionarOrdenacaoAvaliacao(request);
    }

    private void adicionarOrdenacaoAvaliacao(SearchRequest.Builder request) {
        request.sort(sort -> sort.field(field -> field.field("avaliacao").order(SortOrder.Desc)));
        request.sort(sort -> sort.field(field -> field.field("quantidadeAvaliacoes").order(SortOrder.Desc)));
        request.sort(sort -> sort.field(field -> field.field("servicosConcluidos").order(SortOrder.Desc)));
    }

    private void adicionarOrdenacaoDistancia(SearchRequest.Builder request, BuscaProfissionalFiltro filtro) {
        request.sort(sort -> sort.geoDistance(geo -> geo
                .field("localizacao")
                .location(location -> location.latlon(latLon -> latLon
                        .lat(filtro.latitude())
                        .lon(filtro.longitude())))
                .unit(DistanceUnit.Kilometers)
                .order(SortOrder.Asc)));
    }

    private BuscaProfissionalRS.ProfissionalBuscaRS toResponse(
            ProfissionalSearchDocument documento,
            BuscaProfissionalFiltro filtro
    ) {
        if (documento == null) {
            return null;
        }

        List<ProfissionalSearchDocument.ServicoSearch> servicosCompativeis = documento.servicos().stream()
                .filter(servico -> filtro.categoriaId() == null
                        || filtro.categoriaId().equals(servico.categoriaGeralId()))
                .toList();

        Double precoInicial = servicosCompativeis.stream()
                .map(ProfissionalSearchDocument.ServicoSearch::preco)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        List<BuscaProfissionalRS.ServicoBuscaRS> servicos = servicosCompativeis.stream()
                .map(servico -> new BuscaProfissionalRS.ServicoBuscaRS(
                        servico.servicoId(),
                        servico.categoriaGeralId(),
                        servico.categoriaGeral(),
                        servico.categoriaEspecificaId(),
                        servico.categoriaEspecifica(),
                        servico.descricao(),
                        servico.preco(),
                        servico.tipoExecucao()
                ))
                .toList();

        return new BuscaProfissionalRS.ProfissionalBuscaRS(
                documento.profissionalId(),
                documento.nome(),
                documento.fotoPerfil(),
                documento.avaliacao(),
                documento.quantidadeAvaliacoes(),
                documento.servicosConcluidos(),
                documento.disponivel(),
                calcularDistancia(documento.localizacao(), filtro),
                precoInicial,
                documento.cidade(),
                documento.estado(),
                documento.bairro(),
                servicos
        );
    }

    private Double calcularDistancia(
            ProfissionalSearchDocument.LocalizacaoSearch localizacao,
            BuscaProfissionalFiltro filtro
    ) {
        if (localizacao == null || !possuiCoordenadas(filtro)) {
            return null;
        }

        double raioTerraKm = 6371.0088;
        double deltaLatitude = Math.toRadians(localizacao.lat() - filtro.latitude());
        double deltaLongitude = Math.toRadians(localizacao.lon() - filtro.longitude());
        double latitudeOrigem = Math.toRadians(filtro.latitude());
        double latitudeDestino = Math.toRadians(localizacao.lat());

        return calculaDistancia(latitudeOrigem, latitudeDestino, deltaLatitude, deltaLongitude, raioTerraKm);
    }

    private void validar(BuscaProfissionalFiltro filtro) {
        boolean somenteUmaCoordenada = (filtro.latitude() == null) != (filtro.longitude() == null);
        if (somenteUmaCoordenada) {
            throw new BadRequestException("Latitude e longitude devem ser informadas juntas.");
        }
        if (filtro.ordenacao() == OrdenacaoBuscaProfissional.DISTANCIA && !possuiCoordenadas(filtro)) {
            throw new BadRequestException("Latitude e longitude sao obrigatorias para ordenar por distancia.");
        }
    }

    private boolean possuiCoordenadas(BuscaProfissionalFiltro filtro) {
        return filtro.latitude() != null && filtro.longitude() != null;
    }
}
