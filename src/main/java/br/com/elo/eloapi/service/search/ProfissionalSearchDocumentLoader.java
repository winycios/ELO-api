package br.com.elo.eloapi.service.search;

import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.search.ProfissionalSearchDocument;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.repository.AreaAtendimentoRepository;
import br.com.elo.eloapi.repository.ProfissionalRepository;
import br.com.elo.eloapi.repository.ServicoRepository;
import br.com.elo.eloapi.model.storage.EscopoImagem;
import br.com.elo.eloapi.service.storage.ImagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfissionalSearchDocumentLoader {

    private final ProfissionalRepository profissionalRepository;
    private final ServicoRepository servicoRepository;
    private final AreaAtendimentoRepository areaAtendimentoRepository;
    private final ImagemService imagemService;

    @Transactional(readOnly = true)
    public Map<Long, ProfissionalSearchDocument> carregar(Collection<Long> profissionalIds) {
        if (profissionalIds.isEmpty()) {
            return Map.of();
        }

        List<Profissional> profissionais = profissionalRepository.findAllByIdIn(profissionalIds);
        Map<Long, List<Servico>> servicosPorProfissional = servicoRepository
                .findAllByProfissionalIdInAndStAtivoTrue(profissionalIds)
                .stream()
                .collect(Collectors.groupingBy(servico -> servico.getProfissional().getId()));

        Map<Long, AreaAtendimento> areaPorProfissional = areaAtendimentoRepository
                .findAllByProfissionalIdInOrderByIdAsc(profissionalIds)
                .stream()
                .filter(this::possuiCoordenadasValidas)
                .collect(Collectors.toMap(
                        area -> area.getProfissional().getId(),
                        Function.identity(),
                        (primeira, ignorada) -> primeira,
                        LinkedHashMap::new
                ));

        Map<Long, ProfissionalSearchDocument> documentos = new LinkedHashMap<>();
        for (Profissional profissional : profissionais) {
            List<Servico> servicosAtivos = servicosPorProfissional.getOrDefault(profissional.getId(), List.of());
            AreaAtendimento area = areaPorProfissional.get(profissional.getId());
            if (!estaVisivelNaBusca(profissional, servicosAtivos, area)) {
                continue;
            }

            documentos.put(profissional.getId(), toDocument(profissional, area, servicosAtivos));
        }
        return documentos;
    }

    private boolean estaVisivelNaBusca(Profissional profissional, List<Servico> servicosAtivos, AreaAtendimento area) {
        return Boolean.TRUE.equals(profissional.getStDisponivel())
                && !servicosAtivos.isEmpty()
                && possuiCoordenadasValidas(area);
    }

    private boolean possuiCoordenadasValidas(AreaAtendimento area) {
        if (area == null || area.getNrLatitude() == null || area.getNrLongitude() == null) {
            return false;
        }

        double latitude = area.getNrLatitude();
        double longitude = area.getNrLongitude();
        return Double.isFinite(latitude)
                && Double.isFinite(longitude)
                && latitude >= -90.0
                && latitude <= 90.0
                && longitude >= -180.0
                && longitude <= 180.0;
    }

    private ProfissionalSearchDocument toDocument(Profissional profissional, AreaAtendimento area, List<Servico> servicos) {
        String fotoPerfil = profissional.getUriPerfil() != null ? profissional.getUriPerfil() : profissional.getUsuario().getUriPerfil();

        return new ProfissionalSearchDocument(
                profissional.getId(),
                profissional.getUsuario().nomeCompleto(),
                imagemService.urlLeitura(EscopoImagem.PERFIL, fotoPerfil),
                true,
                Boolean.TRUE.equals(profissional.getStDisponivel()),
                profissional.getUsuario().getQtAvaliacaoGeral(),
                valorOuZero(profissional.getUsuario().getQtAvalicaoes()),
                valorOuZero(profissional.getQtServicoConcluido()),
                toLocalizacao(area),
                area == null ? null : area.getNrRaio(),
                area == null ? null : area.getNmCidade(),
                area == null ? null : area.getNmEstado(),
                area == null ? null : area.getNmBairro(),
                servicos.stream().map(this::toServico).toList()
        );
    }

    private ProfissionalSearchDocument.LocalizacaoSearch toLocalizacao(AreaAtendimento area) {
        if (!possuiCoordenadasValidas(area)) {
            return null;
        }
        return new ProfissionalSearchDocument.LocalizacaoSearch(
                area.getNrLatitude(),
                area.getNrLongitude()
        );
    }

    private ProfissionalSearchDocument.ServicoSearch toServico(Servico servico) {
        return new ProfissionalSearchDocument.ServicoSearch(
                servico.getId(),
                servico.getCategoriaEspecifica().getCategoriaGeral().getId(),
                servico.getCategoriaEspecifica().getCategoriaGeral().getNmCategoria(),
                servico.getCategoriaEspecifica().getId(),
                servico.getCategoriaEspecifica().getNmCategoria(),
                servico.getDsDescricao(),
                servico.getDsTag(),
                servico.getVlServico(),
                servico.getTipoServico() == null ? null : servico.getTipoServico().getTipoServico(),
                servico.getTempoExperiencia(),
                true
        );
    }

    private Integer valorOuZero(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
