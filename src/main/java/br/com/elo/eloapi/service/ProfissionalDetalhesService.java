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
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.elo.eloapi.Util.Utils.calcularDistancia;

@Service
@RequiredArgsConstructor
public class ProfissionalDetalhesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfissionalDetalhesService.class);

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
    public List<ProfissionalServicoRS.AvaliacaoRS> buscarDetalhesComentarios(@Positive Long profissionalId, @Positive Long categoriaId) {
        String cacheKey = String.format(RedisStore.KEY_PROFESSIONAL_COMMENTS, profissionalId, categoriaId);

        List<ProfissionalServicoRS.AvaliacaoRS> avaliacaoList = redisStore.buscarNoCacheList(cacheKey, ProfissionalServicoRS.AvaliacaoRS.class).orElse(null);
        if (avaliacaoList == null) {
            avaliacaoList = buscarAvaliacoes(profissionalId, categoriaId);
            redisStore.salvarNoCache(cacheKey, avaliacaoList, RedisStore.CACHE_DURATION);
        }

        return avaliacaoList;
    }

    @Transactional(readOnly = true)
    public ProfissionalServicoRS buscarDetalhes(Long profissionalId, Long servicoId, Usuario usuario, List<Servico> servicos) {
        String cacheKey = String.format(RedisStore.KEY_PROFESSIONAL_DETAILS, profissionalId, servicoId);

        ProfissionalServicoRS detalhes = redisStore.buscarNoCache(cacheKey, ProfissionalServicoRS.class);
        if (detalhes == null) {
            detalhes = buscarNoBanco(profissionalId, servicoId, servicos);
            redisStore.salvarNoCache(cacheKey, detalhes, RedisStore.CACHE_DURATION);
        }

        Endereco origem = null;
        AreaAtendimento destino = null;
        if (usuario != null) {
            origem = enderecoRepository.findByUsuarioIdAndStPrincipalTrue(usuario.getId()).orElse(null);
            destino = areaAtendimentoRepository.findAreaAtendimentoByProfissional_Id(profissionalId).orElse(null);
        }
        return profissionalServicoMapper.comDistancia(detalhes, calcularDistancia(usuario, destino, origem));
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


    private List<ProfissionalServicoRS.AvaliacaoRS> buscarAvaliacoes(Long profissionalId, Long categoriaId) {
        return avaliacaoRepository.findByUsuarioAvaliadoIdAndCategoriaGeralIdOrderByIdDesc(profissionalId, categoriaId, Pageable.unpaged()).stream().map(profissionalServicoMapper::toAvaliacaoResponse).toList();
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
        Long categoriaGeralId = servicoSelecionado.getCategoriaEspecifica().getCategoriaGeral().getId();

        List<Long> servicoIds = servicos.stream().map(Servico::getId).toList();
        Map<Long, List<ServicoImagem>> imagensPorServico = servicoImagemRepository
                .findAllByServicoIdInOrderByServicoIdAscOrdemAsc(servicoIds)
                .stream()
                .collect(Collectors.groupingBy(imagem -> imagem.getServico().getId()));
        Map<Long, List<ServicoDisponibilidade>> disponibilidadesPorServico = servicoDisponibilidadeRepository
                .findAllByServicoIdInAndStAtivoTrueOrderByServicoIdAscDiaSemanaAscHrInicioAsc(servicoIds)
                .stream()
                .collect(Collectors.groupingBy(disponibilidade -> disponibilidade.getServico().getId()));

        List<AvaliacaoReserva> ultimasAvaliacoes = avaliacaoRepository.findByUsuarioAvaliadoIdAndCategoriaGeralIdOrderByIdDesc(profissionalId, categoriaGeralId, PageRequest.of(0, 3));
        long quantidadeAvaliacoesPersistidas = avaliacaoRepository.countByUsuarioAvaliadoIdAndCategoriaGeralId(profissionalId, categoriaGeralId);
        long quantidadePositivas = avaliacaoRepository.countByUsuarioAvaliadoIdAndCategoriaGeralIdAndNotaGreaterThanEqual(profissionalId, categoriaGeralId, 3);

        return profissionalServicoMapper.toResponse(profissional, servicoSelecionado, servicos, imagensPorServico, disponibilidadesPorServico, ultimasAvaliacoes, calcularPercentualPositivas(quantidadeAvaliacoesPersistidas, quantidadePositivas));
    }

    private Double calcularPercentualPositivas(long quantidade, long positivas) {
        if (quantidade == 0) {
            return null;
        }
        return Math.round((positivas * 1000.0) / quantidade) / 10.0;
    }

}
