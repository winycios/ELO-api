package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.avaliacao.AvaliacaoReserva;
import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import br.com.elo.eloapi.model.categoria.CategoriaGeral;
import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.servico.ProfissionalServicoRS;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.servico.ServicoImagem;
import br.com.elo.eloapi.model.servico.mapper.ProfissionalServicoMapper;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfissionalDetalhesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfissionalDetalhesService.class);
    private static final double RAIO_TERRA_KM = 6_371.0088;

    private final ServicoRepository servicoRepository;
    private final ServicoImagemRepository servicoImagemRepository;
    private final ServicoDisponibilidadeRepository servicoDisponibilidadeRepository;
    private final EnderecoRepository enderecoRepository;
    private final AreaAtendimentoRepository areaAtendimentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final AvaliacaoReservaRepository avaliacaoRepository;
    private final RedisStore redisStore;
    private final ProfissionalServicoMapper profissionalServicoMapper;
    private final CategoriaEspecificaRepository categoriaEspecificaRepository;


    @Transactional(readOnly = true)
    public ProfissionalServicoRS buscarDetalhes(Long profissionalId, Long servicoId, Usuario usuario, List<Servico> servicos) {
        String cacheKey = String.format(RedisStore.KEY_PROFESSIONAL_DETAILS, profissionalId, servicoId);

        ProfissionalServicoRS detalhes = buscarNoCache(cacheKey);
        if (detalhes == null) {
            detalhes = buscarNoBanco(profissionalId, servicoId, servicos);
            salvarNoCache(cacheKey, detalhes);
        }

        return profissionalServicoMapper.comDistancia(detalhes, calcularDistancia(usuario, profissionalId));
    }

    @Transactional(readOnly = true)
    public ProfissionalServicoRS buscarDetalhesPorCategoria(Long profissionalId, Long categoriaId, Usuario usuario) {
        CategoriaGeral categoriaGeral = categoriaEspecificaRepository.findById(categoriaId).orElseThrow(() -> new ResourceNotFound("Categoria não encontrada")).getCategoriaGeral();
        List<Servico> servicoList = servicoRepository.findAllByProfissionalIdAndStAtivoTrueAndCategoriaEspecificaCategoriaGeralId(profissionalId, categoriaGeral.getId());

        if (servicoList.isEmpty()) {
            throw new ResourceNotFound("O profissional não possui serviços ativos");
        }

        return buscarDetalhes(profissionalId, servicoList.getFirst().getId(), usuario, servicoList);
    }

    private ProfissionalServicoRS buscarNoBanco(Long profissionalId, Long servicoId, List<Servico> servicos) {
        Profissional profissional = profissionalRepository.findByIdAndStHabilitadoTrue(profissionalId).orElseThrow(() -> new ResourceNotFound("Profissional não encontrado"));

        if (servicos == null || servicos.isEmpty()) {
            CategoriaEspecifica categoriaEspecifica = servicoRepository.findByIdAndStAtivoTrue(servicoId).orElseThrow(() -> new ResourceNotFound("Serviço ativo não encontrado")).getCategoriaEspecifica();
            servicos = servicoRepository.findAllByProfissionalIdAndStAtivoTrueAndCategoriaEspecificaCategoriaGeralId(profissionalId, categoriaEspecifica.getCategoriaGeral().getId());
        }

        if (servicos.isEmpty()) {
            throw new ResourceNotFound("O profissional não possui serviços ativos");
        }

        Servico servicoSelecionado = servicos.stream().filter(servico -> servico.getId().equals(servicoId)).findFirst().orElseThrow(() -> new ResourceNotFound("Serviço ativo não encontrado para este profissional"));

        List<Long> servicoIds = servicos.stream().map(Servico::getId).toList();
        Map<Long, List<ServicoImagem>> imagensPorServico = servicoImagemRepository
                .findAllByServicoIdInOrderByServicoIdAscOrdemAsc(servicoIds)
                .stream()
                .collect(Collectors.groupingBy(imagem -> imagem.getServico().getId()));
        Map<Long, List<ServicoDisponibilidade>> disponibilidadesPorServico = servicoDisponibilidadeRepository
                .findAllByServicoIdInAndStAtivoTrueOrderByServicoIdAscDiaSemanaAscHrInicioAsc(servicoIds)
                .stream()
                .collect(Collectors.groupingBy(disponibilidade -> disponibilidade.getServico().getId()));

        List<AvaliacaoReserva> ultimasAvaliacoes = avaliacaoRepository.findTop3ByUsuarioAvaliadoIdOrderByIdDesc(profissionalId);
        long quantidadeAvaliacoesPersistidas = avaliacaoRepository.countByUsuarioAvaliadoId(profissionalId);
        long quantidadePositivas = avaliacaoRepository.countByUsuarioAvaliadoIdAndNotaGreaterThanEqual(profissionalId, 4);

        return profissionalServicoMapper.toResponse(profissional, servicoSelecionado, servicos, imagensPorServico, disponibilidadesPorServico, ultimasAvaliacoes, calcularPercentualPositivas(quantidadeAvaliacoesPersistidas, quantidadePositivas));
    }

    private ProfissionalServicoRS buscarNoCache(String cacheKey) {
        try {
            return redisStore.find(cacheKey, ProfissionalServicoRS.class).orElse(null);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponível ao consultar detalhes do profissional. Consultando MySQL.", exception);
            return null;
        }
    }

    private void salvarNoCache(String cacheKey, ProfissionalServicoRS detalhes) {
        try {
            redisStore.save(cacheKey, detalhes, RedisStore.CACHE_DURATION);
        } catch (RuntimeException exception) {
            LOGGER.warn("Não foi possível armazenar os detalhes do profissional no Redis.", exception);
        }
    }

    private Double calcularDistancia(Usuario usuario, Long profissionalId) {
        if (usuario == null || usuario.getId() == null) {
            return null;
        }

        Endereco origem = enderecoRepository.findByUsuarioIdAndStPrincipalTrue(usuario.getId()).orElse(null);
        AreaAtendimento destino = areaAtendimentoRepository.findAreaAtendimentoByProfissional_Id(profissionalId).orElse(null);
        if (!possuiCoordenadas(origem) || !possuiCoordenadas(destino)) {
            return null;
        }

        double latitudeOrigem = Math.toRadians(origem.getNrLatitude());
        double latitudeDestino = Math.toRadians(destino.getNrLatitude());
        double diferencaLatitude = latitudeDestino - latitudeOrigem;
        double diferencaLongitude = Math.toRadians(destino.getNrLongitude() - origem.getNrLongitude());
        double haversine = Math.pow(Math.sin(diferencaLatitude / 2), 2)
                + Math.cos(latitudeOrigem)
                * Math.cos(latitudeDestino)
                * Math.pow(Math.sin(diferencaLongitude / 2), 2);
        double distanciaKm = 2 * RAIO_TERRA_KM * Math.asin(Math.sqrt(haversine));
        return Math.round(distanciaKm * 10.0) / 10.0;
    }

    private boolean possuiCoordenadas(Endereco endereco) {
        return endereco != null && coordenadasValidas(endereco.getNrLatitude(), endereco.getNrLongitude());
    }

    private boolean possuiCoordenadas(AreaAtendimento area) {
        return area != null && coordenadasValidas(area.getNrLatitude(), area.getNrLongitude());
    }

    private boolean coordenadasValidas(Double latitude, Double longitude) {
        return latitude != null && longitude != null && Double.isFinite(latitude) && Double.isFinite(longitude) && latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    private Double calcularPercentualPositivas(long quantidade, long positivas) {
        if (quantidade == 0) {
            return null;
        }
        return Math.round((positivas * 1000.0) / quantidade) / 10.0;
    }

}
