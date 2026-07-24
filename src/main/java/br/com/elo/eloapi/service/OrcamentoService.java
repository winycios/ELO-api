package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.exception.ConflictException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.orcamento.OrcamentoEndereco;
import br.com.elo.eloapi.model.orcamento.OrcamentoImagem;
import br.com.elo.eloapi.model.orcamento.dto.HorariosDisponiveisRS;
import br.com.elo.eloapi.model.orcamento.dto.OrcamentoCreateRQ;
import br.com.elo.eloapi.model.orcamento.dto.OrcamentoRS;
import br.com.elo.eloapi.model.orcamento.mapper.OrcamentoMapper;
import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import br.com.elo.eloapi.model.orcamentoStatus.TipoOrcamentoStatus;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.servico.TipoServico;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Set<TipoOrcamentoStatus> STATUS_QUE_OCUPAM_AGENDA = Set.of(TipoOrcamentoStatus.APROVADO, TipoOrcamentoStatus.EM_ANDAMENTO);

    private final ServicoRepository servicoRepository;
    private final ServicoDisponibilidadeRepository servicoDisponibilidadeRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final OrcamentoStatusRepository orcamentoStatusRepository;
    private final OrcamentoImagemRepository orcamentoImagemRepository;
    private final OrcamentoEnderecoRepository orcamentoEnderecoRepository;
    private final EnderecoRepository enderecoRepository;
    private final RedisStore redisStore;

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

        OrcamentoEndereco enderecoSnapshot = endereco == null ? null : orcamentoEnderecoRepository.save(OrcamentoMapper.toEnderecoEntity(endereco, orcamento));

        List<OrcamentoImagem> imagens = orcamentoImagemRepository.saveAll(dto.orcamentoImagemCreateRQList().stream().map(url -> OrcamentoMapper.toImagemEntity(url, orcamento)).toList());

        return OrcamentoMapper.toResponse(orcamento, imagens, enderecoSnapshot);
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
