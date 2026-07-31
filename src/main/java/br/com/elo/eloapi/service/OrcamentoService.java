package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.exception.ConflictException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.avaliacao.AvaliacaoReserva;
import br.com.elo.eloapi.model.avaliacao.dto.AvaliacaoOrcamentoRQ;
import br.com.elo.eloapi.model.avaliacao.dto.AvaliacaoOrcamentoRS;
import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.orcamento.*;
import br.com.elo.eloapi.model.orcamento.dto.*;
import br.com.elo.eloapi.model.orcamento.mapper.OrcamentoMapper;
import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import br.com.elo.eloapi.model.orcamentoStatus.TipoOrcamentoStatus;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.publicacao.dto.CursorPageRS;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.servico.TipoServico;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.*;
import br.com.elo.eloapi.service.search.SearchOutboxService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcamentoService.class);
    private static final Duration INTERVALO_HORARIOS = Duration.ofMinutes(30);
    private static final Duration MARGEM_APOS_RESERVA = Duration.ofHours(1);
    private static final Set<TipoOrcamentoStatus> STATUS_QUE_OCUPAM_AGENDA = Set.of(
            TipoOrcamentoStatus.ORCAMENTO_FINAL,
            TipoOrcamentoStatus.APROVADO
    );

    private final ServicoRepository servicoRepository;
    private final ServicoDisponibilidadeRepository servicoDisponibilidadeRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final OrcamentoStatusRepository orcamentoStatusRepository;
    private final OrcamentoImagemRepository orcamentoImagemRepository;
    private final OrcamentoEnderecoRepository orcamentoEnderecoRepository;
    private final OrcamentoCustoRepository orcamentoCustoRepository;
    private final EnderecoRepository enderecoRepository;
    private final RedisStore redisStore;
    private final CursorCodec cursorCodec;
    private final AreaAtendimentoRepository areaAtendimentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final AvaliacaoReservaRepository avaliacaoReservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SearchOutboxService searchOutboxService;

    @Transactional(readOnly = true)
    public HorariosDisponiveisRS buscarHorariosDisponiveis(Long servicoId, LocalDate dataReferencia) {
        Servico servico = buscarServicoDisponivel(servicoId);
        LocalDate referencia = dataReferencia == null ? LocalDate.now() : dataReferencia;
        LocalDate inicioSemana = referencia.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate fimSemana = inicioSemana.plusDays(6);
        if (fimSemana.isBefore(LocalDate.now())) {
            throw new BadRequestException("Não é possível consultar uma semana que já terminou.");
        }

        Long profissionalId = servico.getProfissional().getId();
        String cacheKey = String.format(RedisStore.KEY_AVAILABLE_HOURS, profissionalId, servicoId, inicioSemana);

        HorariosDisponiveisRS cache = buscarHorariosNoCache(cacheKey);
        if (cache != null) {
            return cache;
        }

        HorariosDisponiveisRS response = calcularHorariosDisponiveis(servico, inicioSemana, LocalDateTime.now());
        salvarHorariosNoCache(cacheKey, response);
        return response;
    }

    @Transactional
    public OrcamentoRS solicitarOrcamento(Usuario cliente, OrcamentoCreateRQ dto) {
        Servico servico = buscarServicoDisponivel(dto.idServico());
        validarHorarioPreferido(servico, dto.dtPreferidoSolicitado());

        Endereco endereco = buscarEnderecoDaSolicitacao(cliente, servico, dto.idEndereco());
        OrcamentoStatus statusPendente = orcamentoStatusRepository.findByTipoOrcamentoStatus(TipoOrcamentoStatus.PENDENTE).orElseThrow(() -> new IllegalStateException("O status pendente não está configurado."));

        Orcamento orcamento = orcamentoRepository.save(OrcamentoMapper.toEntity(dto, servico, cliente, statusPendente));

        OrcamentoEndereco enderecoSnapshot = endereco == null ? null : orcamentoEnderecoRepository.save(OrcamentoMapper.toOrcamentoEnderecoEntity(endereco, orcamento));

        List<OrcamentoImagem> imagens = orcamentoImagemRepository.saveAll(dto.orcamentoImagemCreateRQList().stream().map(url -> OrcamentoMapper.toImagemEntity(url, orcamento)).toList());

        return OrcamentoMapper.toResponse(orcamento, imagens, enderecoSnapshot);
    }

    @Transactional(readOnly = true)
    public CursorPageRS<OrcamentoListagemRS> listarOrcamentos(Usuario cliente, String filtroStatus, String cursor, int tamanho) {
        TipoOrcamentoStatus status = buscarStatusDoFiltro(filtroStatus);
        Long cursorId = cursorCodec.decodeId(cursor);
        List<Orcamento> encontrados = orcamentoRepository.listarPorCliente(cliente.getId(), status, cursorId, PageRequest.of(0, tamanho + 1));
        boolean hasNext = encontrados.size() > tamanho;
        List<Orcamento> pagina = encontrados.stream().limit(tamanho).toList();
        String nextCursor = hasNext ? cursorCodec.encodeId(pagina.getLast().getId()) : null;
        Set<Long> orcamentosAvaliados = buscarOrcamentosAvaliados(pagina, cliente.getId());

        return new CursorPageRS<>(
                pagina.stream()
                        .map(orcamento -> OrcamentoMapper.toListagemResponse(
                                orcamento,
                                estaConcluido(orcamento) && orcamentosAvaliados.contains(orcamento.getId())
                        ))
                        .toList(),
                nextCursor,
                hasNext
        );
    }

    @Transactional(readOnly = true)
    public CursorPageRS<OrcamentoListagemProfissionalRS> listarOrcamentosProfissional(Usuario profissional, String filtroStatus, String cursor, int tamanho) {
        Set<TipoOrcamentoStatus> status = buscarStatusDoFiltroProfissional(filtroStatus);
        Long cursorId = cursorCodec.decodeId(cursor);
        List<Orcamento> encontrados = orcamentoRepository.listarPorProfissional(profissional.getId(), status, cursorId, PageRequest.of(0, tamanho + 1));
        boolean hasNext = encontrados.size() > tamanho;
        List<Orcamento> pagina = encontrados.stream().limit(tamanho).toList();
        String nextCursor = hasNext ? cursorCodec.encodeId(pagina.getLast().getId()) : null;
        AreaAtendimento areaAtendimento = areaAtendimentoRepository.findAreaAtendimentoByProfissional_Id(profissional.getId()).orElse(null);
        Map<Long, List<OrcamentoCusto>> custosPorOrcamento = buscarCustosPorOrcamento(pagina);
        Set<Long> orcamentosAvaliados = buscarOrcamentosAvaliados(pagina, profissional.getId());

        return new CursorPageRS<>(
                pagina.stream()
                        .map(orcamento -> OrcamentoMapper.orcamentoListagemProfissionalResponse(
                                orcamento,
                                areaAtendimento,
                                custosPorOrcamento.getOrDefault(orcamento.getId(), List.of()),
                                estaConcluido(orcamento) && orcamentosAvaliados.contains(orcamento.getId())))
                        .toList(),
                nextCursor,
                hasNext
        );
    }

    @Transactional(readOnly = true)
    public OrcamentoDetalheRS buscarOrcamentoPorId(Usuario cliente, Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findByIdAndUsuarioId(orcamentoId, cliente.getId()).orElseThrow(() -> new ResourceNotFound("Orçamento não encontrado."));
        List<OrcamentoImagem> imagens = orcamentoImagemRepository.findAllByOrcamentoIdOrderByIdAsc(orcamentoId);
        OrcamentoEndereco endereco = orcamentoEnderecoRepository.findFirstByOrcamentoIdOrderByIdAsc(orcamentoId).orElse(null);
        List<OrcamentoCusto> custos = orcamentoCustoRepository.findAllByOrcamentoIdOrderByIdAsc(orcamentoId);

        return OrcamentoMapper.toDetalheResponse(orcamento, imagens, endereco, custos);
    }

    @Transactional(readOnly = true)
    public OrcamentoDetalheProfissionalRS buscarOrcamentoPorIdProfissional(Usuario profissional, Long orcamentoId) {
        Orcamento orcamento = buscarOrcamentoDoProfissional(profissional.getId(), orcamentoId);
        return montarDetalheProfissional(orcamento);
    }

    @Transactional
    public OrcamentoDetalheProfissionalRS enviarOrcamentoFinal(Usuario usuarioProfissional, Long orcamentoId, OrcamentoFinalCreateRQ dto) {
        Profissional profissional = profissionalRepository.findByIdForUpdate(usuarioProfissional.getId())
                .orElseThrow(() -> new ResourceNotFound("Perfil profissional não encontrado."));
        Orcamento orcamento = buscarOrcamentoDoProfissional(profissional.getId(), orcamentoId);

        validarStatusAtual(orcamento, TipoOrcamentoStatus.PENDENTE, "Somente solicitações pendentes podem receber um orçamento final.");
        validarIntervaloProposto(orcamento, dto.inicioProposto(), dto.fimProposto());

        OrcamentoStatus statusFinal = buscarStatusConfigurado(TipoOrcamentoStatus.ORCAMENTO_FINAL);
        orcamento.setDtInicioProposto(dto.inicioProposto());
        orcamento.setDtFimProposto(dto.fimProposto());
        orcamento.setDsObservacaoProfissional(normalizarTextoOpcional(dto.observacaoProfissional()));
        orcamento.setOrcamentoStatus(statusFinal);
        orcamentoRepository.save(orcamento);

        List<OrcamentoCusto> custos = orcamentoCustoRepository.saveAll(
                dto.custos().stream()
                        .map(custo -> new OrcamentoCusto(
                                null,
                                orcamento,
                                custo.descricao().trim(),
                                custo.valor()
                        ))
                        .toList()
        );

        invalidarCacheHorarios(profissional.getId());
        return montarDetalheProfissional(orcamento, custos);
    }

    @Transactional
    public OrcamentoDetalheProfissionalRS recusarOrcamento(Usuario profissional, Long orcamentoId, OrcamentoCancelamentoRQ dto) {
        Orcamento orcamento = buscarOrcamentoDoProfissional(profissional.getId(), orcamentoId);
        validarStatusAtual(orcamento, TipoOrcamentoStatus.PENDENTE, "Somente solicitações pendentes podem ser recusadas.");
        registrarCancelamento(orcamento, TipoAutorCancelamento.PROFISSIONAL, profissional, dto.motivo(), dto.descricao());
        orcamentoRepository.save(orcamento);
        return montarDetalheProfissional(orcamento);
    }

    @Transactional
    public OrcamentoDetalheRS cancelarOrcamentoCliente(Usuario cliente, Long orcamentoId, OrcamentoCancelamentoRQ dto) {
        Orcamento orcamento = orcamentoRepository.findByIdAndUsuarioId(orcamentoId, cliente.getId()).orElseThrow(() -> new ResourceNotFound("Orçamento não encontrado."));
        TipoOrcamentoStatus statusAtual = orcamento.getOrcamentoStatus().getTipoOrcamentoStatus();

        if (!EnumSet.of(TipoOrcamentoStatus.PENDENTE, TipoOrcamentoStatus.ORCAMENTO_FINAL, TipoOrcamentoStatus.APROVADO).contains(statusAtual)) {
            throw new ConflictException("Este orçamento não pode mais ser cancelado pelo usuário.");
        }

        registrarCancelamento(orcamento, TipoAutorCancelamento.USUARIO, cliente, dto.motivo(), dto.descricao());
        orcamentoRepository.save(orcamento);
        invalidarCacheHorarios(orcamento.getServico().getProfissional().getId());
        return montarDetalheCliente(orcamento);
    }

    // já deixei pronto pois sei que um dia irá existir
    @Transactional
    public void cancelarOrcamentoPorExpiracao(Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId).orElseThrow(() -> new ResourceNotFound("Orçamento não encontrado."));

        TipoOrcamentoStatus statusAtual = orcamento.getOrcamentoStatus().getTipoOrcamentoStatus();
        if (!EnumSet.of(TipoOrcamentoStatus.PENDENTE, TipoOrcamentoStatus.ORCAMENTO_FINAL).contains(statusAtual)) {
            throw new ConflictException("Este orçamento não está em um status que permita expiração.");
        }

        registrarCancelamento(orcamento, TipoAutorCancelamento.SISTEMA, null, "expirado", "Orçamento cancelado automaticamente por expiração.");
        orcamentoRepository.save(orcamento);
        invalidarCacheHorarios(orcamento.getServico().getProfissional().getId());
    }

    @Transactional
    public OrcamentoDetalheRS aprovarOrcamentoFinal(Usuario cliente, Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findByIdAndUsuarioId(orcamentoId, cliente.getId())
                .orElseThrow(() -> new ResourceNotFound("Orçamento não encontrado."));
        validarStatusAtual(orcamento, TipoOrcamentoStatus.ORCAMENTO_FINAL, "Somente um orçamento final aguardando aprovação pode ser aprovado.");
        orcamento.setOrcamentoStatus(buscarStatusConfigurado(TipoOrcamentoStatus.APROVADO));
        orcamentoRepository.save(orcamento);

        List<OrcamentoImagem> imagens = orcamentoImagemRepository.findAllByOrcamentoIdOrderByIdAsc(orcamentoId);
        OrcamentoEndereco endereco = orcamentoEnderecoRepository.findFirstByOrcamentoIdOrderByIdAsc(orcamentoId).orElse(null);
        List<OrcamentoCusto> custos = orcamentoCustoRepository.findAllByOrcamentoIdOrderByIdAsc(orcamentoId);
        return OrcamentoMapper.toDetalheResponse(orcamento, imagens, endereco, custos);
    }

    @Transactional
    public OrcamentoDetalheProfissionalRS concluirOrcamento(Usuario usuarioProfissional, Long orcamentoId, OrcamentoConclusaoRQ dto) {
        Profissional profissional = profissionalRepository.findByIdForUpdate(usuarioProfissional.getId()).orElseThrow(() -> new ResourceNotFound("Perfil profissional não encontrado."));
        Orcamento orcamento = buscarOrcamentoDoProfissional(profissional.getId(), orcamentoId);

        validarStatusAtual(orcamento, TipoOrcamentoStatus.APROVADO, "Somente um orçamento aprovado pode ser concluído.");
        if (orcamento.getDtFimProposto() == null) {
            throw new ConflictException("O orçamento aprovado não possui um horário final definido.");
        }

        LocalDateTime agora = LocalDateTime.now();
        if (agora.isBefore(orcamento.getDtFimProposto())) {
            throw new ConflictException("O serviço só pode ser concluído após o horário final acordado.");
        }

        orcamento.setUsuarioConclusao(usuarioProfissional);
        orcamento.setDsObservacaoConclusao(normalizarTextoOpcional(dto.observacao()));
        orcamento.setDtConclusao(agora);
        orcamento.setOrcamentoStatus(buscarStatusConfigurado(TipoOrcamentoStatus.CONCLUIDO));

        profissional.setQtServicoConcluido(Optional.ofNullable(profissional.getQtServicoConcluido()).orElse(0) + 1);

        orcamentoRepository.save(orcamento);
        profissionalRepository.save(profissional);
        searchOutboxService.solicitarReindexacao(profissional.getId());
        invalidarCacheHorarios(profissional.getId());
        return montarDetalheProfissional(orcamento);
    }

    @Transactional
    public AvaliacaoOrcamentoRS avaliarProfissional(Usuario cliente, Long orcamentoId, AvaliacaoOrcamentoRQ dto) {
        Orcamento orcamento = orcamentoRepository.findByIdAndUsuarioId(orcamentoId, cliente.getId()).orElseThrow(() -> new ResourceNotFound("Orçamento não encontrado."));
        Usuario profissionalAvaliado = orcamento.getServico().getProfissional().getUsuario();

        AvaliacaoOrcamentoRS response = registrarAvaliacao(orcamento, cliente, profissionalAvaliado, dto);
        searchOutboxService.solicitarReindexacao(orcamento.getServico().getProfissional().getId());
        return response;
    }

    @Transactional
    public AvaliacaoOrcamentoRS avaliarCliente(Usuario profissional, Long orcamentoId, AvaliacaoOrcamentoRQ dto) {
        Orcamento orcamento = buscarOrcamentoDoProfissional(profissional.getId(), orcamentoId);
        return registrarAvaliacao(orcamento, profissional, orcamento.getUsuario(), dto);
    }

    private OrcamentoDetalheRS montarDetalheCliente(Orcamento orcamento) {
        Long orcamentoId = orcamento.getId();
        List<OrcamentoImagem> imagens = orcamentoImagemRepository.findAllByOrcamentoIdOrderByIdAsc(orcamentoId);
        OrcamentoEndereco endereco = orcamentoEnderecoRepository.findFirstByOrcamentoIdOrderByIdAsc(orcamentoId).orElse(null);
        List<OrcamentoCusto> custos = orcamentoCustoRepository.findAllByOrcamentoIdOrderByIdAsc(orcamentoId);
        return OrcamentoMapper.toDetalheResponse(orcamento, imagens, endereco, custos);
    }

    private void registrarCancelamento(Orcamento orcamento, TipoAutorCancelamento autor, Usuario usuarioResponsavel, String motivo, String descricao) {
        orcamento.setMotivoCancelamento(motivo.trim());
        orcamento.setDsDescricaoCancelamento(descricao.trim());
        orcamento.setAutorCancelamento(autor);
        orcamento.setUsuarioCancelamento(usuarioResponsavel);
        orcamento.setDtCancelamento(LocalDateTime.now());
        orcamento.setOrcamentoStatus(buscarStatusConfigurado(TipoOrcamentoStatus.CANCELADO));
    }

    private AvaliacaoOrcamentoRS registrarAvaliacao(Orcamento orcamento, Usuario avaliador, Usuario usuarioAvaliado, AvaliacaoOrcamentoRQ dto) {
        validarStatusAtual(orcamento, TipoOrcamentoStatus.CONCLUIDO, "A avaliação só pode ser enviada após a conclusão do serviço.");

        if (Objects.equals(avaliador.getId(), usuarioAvaliado.getId())) {
            throw new ConflictException("Não é possível avaliar o próprio usuário.");
        }
        if (avaliacaoReservaRepository.existsByReservaIdAndAvaliadorId(orcamento.getId(), avaliador.getId())) {
            throw new ConflictException("Você já avaliou este serviço.");
        }

        Usuario usuarioAvaliadoBloqueado = usuarioRepository.findByIdForUpdate(usuarioAvaliado.getId()).orElseThrow(() -> new ResourceNotFound("Usuário avaliado não encontrado."));

        AvaliacaoReserva avaliacao = new AvaliacaoReserva();
        avaliacao.setReservaId(orcamento.getId());
        avaliacao.setAvaliador(avaliador);
        avaliacao.setUsuarioAvaliado(usuarioAvaliadoBloqueado);
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(normalizarTextoOpcional(dto.comentario()));

        try {
            avaliacaoReservaRepository.saveAndFlush(avaliacao);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Você já avaliou este serviço.");
        }

        int quantidadeAtual = Optional.ofNullable(usuarioAvaliadoBloqueado.getQtAvalicaoes()).orElse(0);
        double mediaAtual = Optional.ofNullable(usuarioAvaliadoBloqueado.getQtAvaliacaoGeral()).orElse(0.0);
        double novaMedia = ((mediaAtual * quantidadeAtual) + dto.nota()) / (quantidadeAtual + 1);

        usuarioAvaliadoBloqueado.setQtAvalicaoes(quantidadeAtual + 1);
        usuarioAvaliadoBloqueado.setQtAvaliacaoGeral(Math.round(novaMedia * 100.0) / 100.0);
        usuarioRepository.save(usuarioAvaliadoBloqueado);

        return new AvaliacaoOrcamentoRS(avaliacao.getId(), orcamento.getId(), avaliador.getId(), usuarioAvaliadoBloqueado.getId(), avaliacao.getNota(), avaliacao.getComentario(), avaliacao.getDtCriacao()
        );
    }

    private Set<Long> buscarOrcamentosAvaliados(List<Orcamento> orcamentos, Long avaliadorId) {
        if (orcamentos.isEmpty()) {
            return Set.of();
        }

        List<Long> ids = orcamentos.stream().map(Orcamento::getId).toList();
        return Set.copyOf(avaliacaoReservaRepository.findOrcamentoIdsAvaliados(ids, avaliadorId));
    }

    private boolean estaConcluido(Orcamento orcamento) {
        return orcamento.getOrcamentoStatus().getTipoOrcamentoStatus().isConcluido();
    }

    private Map<Long, List<OrcamentoCusto>> buscarCustosPorOrcamento(List<Orcamento> orcamentos) {
        if (orcamentos.isEmpty()) {
            return Map.of();
        }

        List<Long> ids = orcamentos.stream().map(Orcamento::getId).toList();
        return orcamentoCustoRepository.findAllByOrcamentoIdInOrderByOrcamentoIdAscIdAsc(ids).stream().collect(Collectors.groupingBy(custo -> custo.getOrcamento().getId()));
    }

    private Orcamento buscarOrcamentoDoProfissional(Long profissionalId, Long orcamentoId) {
        return orcamentoRepository.findByIdAndServicoProfissionalId(orcamentoId, profissionalId).orElseThrow(() -> new ResourceNotFound("Orçamento não encontrado para este profissional."));
    }

    private OrcamentoDetalheProfissionalRS montarDetalheProfissional(Orcamento orcamento) {
        List<OrcamentoCusto> custos = orcamentoCustoRepository.findAllByOrcamentoIdOrderByIdAsc(orcamento.getId());
        return montarDetalheProfissional(orcamento, custos);
    }

    private OrcamentoDetalheProfissionalRS montarDetalheProfissional(Orcamento orcamento, List<OrcamentoCusto> custos) {
        Long orcamentoId = orcamento.getId();
        List<OrcamentoImagem> imagens = orcamentoImagemRepository.findAllByOrcamentoIdOrderByIdAsc(orcamentoId);
        OrcamentoEndereco endereco = orcamentoEnderecoRepository.findFirstByOrcamentoIdOrderByIdAsc(orcamentoId).orElse(null);
        AreaAtendimento areaAtendimento = areaAtendimentoRepository.findAreaAtendimentoByProfissional_Id(orcamento.getServico().getProfissional().getId()).orElse(null);
        return OrcamentoMapper.toDetalheProfissionalResponse(orcamento, imagens, endereco, custos, areaAtendimento);
    }

    private void validarIntervaloProposto(Orcamento orcamento, LocalDateTime inicioProposto, LocalDateTime fimProposto) {
        if (!inicioProposto.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("O início proposto deve estar no futuro.");
        }
        if (!fimProposto.isAfter(inicioProposto)) {
            throw new BadRequestException("O fim proposto deve ser posterior ao início proposto.");
        }
        if (alinhadoAoIntervaloDaAgenda(inicioProposto) || alinhadoAoIntervaloDaAgenda(fimProposto)) {
            throw new BadRequestException("Início e fim devem respeitar intervalos de 30 minutos.");
        }

        validarHorarioPreferido(orcamento.getServico(), inicioProposto);

        long conflitos = orcamentoRepository.contarConflitosAgenda(orcamento.getServico().getProfissional().getId(), orcamento.getId(), STATUS_QUE_OCUPAM_AGENDA, inicioProposto.minus(MARGEM_APOS_RESERVA), fimProposto.plus(MARGEM_APOS_RESERVA));
        if (conflitos > 0) {
            throw new ConflictException("O intervalo proposto conflita com outro serviço ou orçamento do profissional.");
        }
    }

    private boolean alinhadoAoIntervaloDaAgenda(LocalDateTime horario) {
        return horario.getMinute() % INTERVALO_HORARIOS.toMinutes() != 0 || horario.getSecond() != 0 || horario.getNano() != 0;
    }

    private void validarStatusAtual(Orcamento orcamento, TipoOrcamentoStatus statusEsperado, String mensagem) {
        if (orcamento.getOrcamentoStatus().getTipoOrcamentoStatus() != statusEsperado) {
            throw new ConflictException(mensagem);
        }
    }

    private OrcamentoStatus buscarStatusConfigurado(TipoOrcamentoStatus status) {
        return orcamentoStatusRepository.findByTipoOrcamentoStatus(status).orElseThrow(() -> new IllegalStateException("O status " + status.getDescricao() + " não está configurado."));
    }

    private String normalizarTextoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private void invalidarCacheHorarios(Long profissionalId) {
        try {
            redisStore.deleteByPattern(String.format(RedisStore.KEY_AVAILABLE_HOURS_PATTERN, profissionalId));
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponível ao invalidar horários do profissional {}.", profissionalId, exception);
        }
    }

    private HorariosDisponiveisRS calcularHorariosDisponiveis(Servico servico, LocalDate inicioSemana, LocalDateTime agora) {
        List<ServicoDisponibilidade> disponibilidades = servicoDisponibilidadeRepository.findAllByServicoIdAndStAtivoTrueOrderByDiaSemanaAscHrInicioAsc(servico.getId());
        Map<Integer, List<ServicoDisponibilidade>> disponibilidadesPorDia = disponibilidades.stream().collect(Collectors.groupingBy(ServicoDisponibilidade::getDiaSemana));
        List<Intervalo> bloqueios = buscarBloqueios(servico.getProfissional().getId(), inicioSemana, inicioSemana.plusDays(7));
        List<HorariosDisponiveisRS.DiaHorariosRS> dias = new ArrayList<>(7);

        for (int indiceDia = 0; indiceDia < 7; indiceDia++) {
            LocalDate data = inicioSemana.plusDays(indiceDia);
            List<ServicoDisponibilidade> expediente = disponibilidadesPorDia.getOrDefault(data.getDayOfWeek().getValue(), List.of());
            dias.add(montarHorariosDoDia(data, expediente, bloqueios, agora));
        }

        return new HorariosDisponiveisRS(servico.getId(), inicioSemana, inicioSemana.plusDays(6), Math.toIntExact(INTERVALO_HORARIOS.toMinutes()), Math.toIntExact(MARGEM_APOS_RESERVA.toMinutes()), List.copyOf(dias));
    }

    private HorariosDisponiveisRS.DiaHorariosRS montarHorariosDoDia(LocalDate data, List<ServicoDisponibilidade> expediente, List<Intervalo> bloqueios, LocalDateTime agora) {
        TreeSet<LocalTime> horarios = new TreeSet<>();

        for (ServicoDisponibilidade disponibilidade : expediente) {
            LocalDateTime candidato = data.atTime(disponibilidade.getHrInicio());
            LocalDateTime fimDisponibilidade = data.atTime(disponibilidade.getHrFim());

            while (candidato.isBefore(fimDisponibilidade)) {
                LocalDateTime horarioAtual = candidato;
                if (horarioAtual.isAfter(agora) && bloqueios.stream().noneMatch(bloqueio -> bloqueio.contem(horarioAtual))) {
                    horarios.add(horarioAtual.toLocalTime());
                }
                candidato = candidato.plus(INTERVALO_HORARIOS);
            }
        }

        List<HorariosDisponiveisRS.FaixaTrabalhoRS> faixas = expediente.stream().map(disponibilidade -> new HorariosDisponiveisRS.FaixaTrabalhoRS(disponibilidade.getHrInicio(), disponibilidade.getHrFim())).toList();

        return new HorariosDisponiveisRS.DiaHorariosRS(data, data.getDayOfWeek().getValue(), faixas, List.copyOf(horarios));
    }

    private void validarHorarioPreferido(Servico servico, LocalDateTime horarioPreferido) {
        LocalDateTime agora = LocalDateTime.now();
        if (!horarioPreferido.isAfter(agora)) {
            throw new BadRequestException("O horário preferido deve estar no futuro.");
        }

        List<ServicoDisponibilidade> disponibilidades = buscarDisponibilidades(servico.getId(), horarioPreferido.toLocalDate());
        boolean pertenceAUmHorarioInformado = disponibilidades.stream().anyMatch(disponibilidade -> possuiHorario(disponibilidade, horarioPreferido));

        if (!pertenceAUmHorarioInformado) {
            throw new BadRequestException("O horário preferido não pertence à disponibilidade informada pelo profissional.");
        }

        boolean bloqueado = buscarBloqueios(servico.getProfissional().getId(), horarioPreferido.toLocalDate(), horarioPreferido.toLocalDate().plusDays(1)).stream().anyMatch(intervalo -> intervalo.contem(horarioPreferido));

        if (bloqueado) {
            throw new ConflictException("O horário preferido está indisponível. Escolha outro horário considerando o deslocamento do profissional.");
        }
    }

    private boolean possuiHorario(ServicoDisponibilidade disponibilidade, LocalDateTime horarioPreferido) {
        LocalDateTime candidato = horarioPreferido.toLocalDate().atTime(disponibilidade.getHrInicio());
        LocalDateTime fim = horarioPreferido.toLocalDate().atTime(disponibilidade.getHrFim());
        while (candidato.isBefore(fim)) {
            if (candidato.equals(horarioPreferido)) {
                return true;
            }
            candidato = candidato.plus(INTERVALO_HORARIOS);
        }
        return false;
    }

    private List<ServicoDisponibilidade> buscarDisponibilidades(Long servicoId, LocalDate data) {
        return servicoDisponibilidadeRepository.findAllByServicoIdAndDiaSemanaAndStAtivoTrueOrderByHrInicioAsc(servicoId, data.getDayOfWeek().getValue());
    }

    private List<Intervalo> buscarBloqueios(Long profissionalId, LocalDate inicioPeriodo, LocalDate fimPeriodoExclusivo) {
        LocalDateTime inicio = inicioPeriodo.atStartOfDay();
        LocalDateTime fim = fimPeriodoExclusivo.atStartOfDay();

        return orcamentoRepository.buscarIntervalosOcupados(profissionalId, STATUS_QUE_OCUPAM_AGENDA, inicio.minus(MARGEM_APOS_RESERVA), fim).stream().map(intervalo -> new Intervalo(intervalo.getInicio(), intervalo.getFim().plus(MARGEM_APOS_RESERVA))).sorted(Comparator.comparing(Intervalo::inicio)).toList();
    }

    private Servico buscarServicoDisponivel(Long servicoId) {
        Servico servico = servicoRepository.findByIdAndStAtivoTrue(servicoId).orElseThrow(() -> new ResourceNotFound("Serviço ativo não encontrado."));

        if (!Boolean.TRUE.equals(servico.getProfissional().getStHabilitado()) || !Boolean.TRUE.equals(servico.getProfissional().getStDisponivel())) {
            throw new ConflictException("O profissional não está disponível para receber solicitações.");
        }
        return servico;
    }

    private Endereco buscarEnderecoDaSolicitacao(Usuario cliente, Servico servico, Long enderecoId) {
        if (enderecoId == null) {
            if (servico.getTipoServico() == TipoServico.PRESENCIAL) {
                throw new BadRequestException("O endereço é obrigatório para serviços presenciais.");
            }
            return null;
        }

        return enderecoRepository.findByIdAndUsuarioIdAndStAtivoTrue(enderecoId, cliente.getId()).orElseThrow(() -> new ResourceNotFound("Endereço ativo não encontrado para este usuário."));
    }

    private TipoOrcamentoStatus buscarStatusDoFiltro(String filtroStatus) {
        if (filtroStatus == null || filtroStatus.isBlank() || filtroStatus.equalsIgnoreCase("todos")) {
            return null;
        }

        try {
            return TipoOrcamentoStatus.buscarTipo(filtroStatus);
        } catch (IllegalArgumentException exception) {
            String statusValidos = Arrays.stream(TipoOrcamentoStatus.values())
                    .map(TipoOrcamentoStatus::getDescricao)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Status inválido. Valores aceitos: " + statusValidos + ".");
        }
    }

    private Set<TipoOrcamentoStatus> buscarStatusDoFiltroProfissional(String filtroStatus) {
        if (filtroStatus == null || filtroStatus.isBlank() || filtroStatus.equalsIgnoreCase("todos")) {
            return EnumSet.allOf(TipoOrcamentoStatus.class);
        }

        String filtroNormalizado = filtroStatus.trim().toLowerCase(Locale.ROOT);
        return switch (filtroNormalizado) {
            case "novo", "novos" -> EnumSet.of(
                    TipoOrcamentoStatus.PENDENTE
            );
            case "enviado" -> EnumSet.of(
                    TipoOrcamentoStatus.ORCAMENTO_FINAL
            );
            case "aprovado", "aprovados" -> EnumSet.of(
                    TipoOrcamentoStatus.APROVADO
            );
            case "historico", "histórico" -> EnumSet.of(
                    TipoOrcamentoStatus.CONCLUIDO,
                    TipoOrcamentoStatus.CANCELADO
            );
            default -> throw new BadRequestException("Filtro inválido. Use novos, aprovados, historico, todos ou um status individual.");
        };
    }

    private HorariosDisponiveisRS buscarHorariosNoCache(String cacheKey) {
        try {
            return redisStore.find(cacheKey, HorariosDisponiveisRS.class).orElse(null);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponível ao consultar horários. Consultando o MySQL.", exception);
            return null;
        }
    }

    private void salvarHorariosNoCache(String cacheKey, HorariosDisponiveisRS response) {
        try {
            redisStore.save(cacheKey, response, RedisStore.AVAILABLE_HOURS_CACHE_DURATION);
        } catch (RuntimeException exception) {
            LOGGER.warn("Não foi possível armazenar os horários disponíveis no Redis.", exception);
        }
    }

    private record Intervalo(LocalDateTime inicio, LocalDateTime fim) {
        boolean contem(LocalDateTime horario) {
            return !horario.isBefore(inicio) && horario.isBefore(fim);
        }
    }
}
