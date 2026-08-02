package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.orcamento.TipoAutorCancelamento;
import br.com.elo.eloapi.model.orcamentoStatus.TipoOrcamentoStatus;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.profissional.dto.ProfissionalDashboardRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.OrcamentoRepository;
import br.com.elo.eloapi.repository.ProfissionalRepository;
import br.com.elo.eloapi.repository.RedisStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfissionalDashboardService {

    private final ProfissionalRepository profissionalRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final RedisStore redisStore;
    private final Clock clock;

    @Transactional(readOnly = true)
    public ProfissionalDashboardRS buscarDashboard(Usuario usuario) {
        LocalDate hoje = LocalDate.now(clock);
        String cacheKey = String.format(RedisStore.KEY_PROFESSIONAL_DASHBOARD, usuario.getId(), hoje);

        ProfissionalDashboardRS cache = redisStore.buscarNoCache(cacheKey, ProfissionalDashboardRS.class);
        if (cache != null) {
            return cache;
        }

        Profissional profissional = profissionalRepository.findById(usuario.getId()).orElseThrow(() -> new ResourceNotFound("Perfil profissional não encontrado."));
        ProfissionalDashboardRS dashboard = montarDashboard(profissional, hoje);
        redisStore.salvarNoCache(cacheKey, dashboard, RedisStore.AVAILABLE_TWELVE_HOURS_CACHE_DURATION);
        return dashboard;
    }

    private ProfissionalDashboardRS montarDashboard(Profissional profissional, LocalDate hoje) {
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate inicioProximoMes = inicioMes.plusMonths(1);
        LocalDate inicioMesAnterior = inicioMes.minusMonths(1);
        LocalDate inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate inicioSemanaAnterior = inicioSemana.minusWeeks(1);
        LocalDate inicioConsulta = inicioMesAnterior.isBefore(inicioSemanaAnterior) ? inicioMesAnterior : inicioSemanaAnterior;

        OrcamentoRepository.DashboardContadores contadores = orcamentoRepository.buscarContadoresDashboard(
                profissional.getId(),
                TipoOrcamentoStatus.PENDENTE,
                EnumSet.of(TipoOrcamentoStatus.APROVADO, TipoOrcamentoStatus.CONCLUIDO),
                EnumSet.of(TipoOrcamentoStatus.ORCAMENTO_FINAL, TipoOrcamentoStatus.APROVADO, TipoOrcamentoStatus.CONCLUIDO),
                TipoOrcamentoStatus.CANCELADO,
                TipoAutorCancelamento.PROFISSIONAL,
                hoje.atStartOfDay(),
                hoje.plusDays(1).atStartOfDay(),
                inicioMes.atStartOfDay(),
                inicioProximoMes.atStartOfDay()
        );

        List<OrcamentoRepository.DashboardServicoConcluido> concluidos =
                orcamentoRepository.buscarServicosConcluidosDashboard(
                        profissional.getId(),
                        TipoOrcamentoStatus.CONCLUIDO,
                        inicioConsulta.atStartOfDay(),
                        inicioProximoMes.atStartOfDay()
                );

        List<OrcamentoRepository.DashboardServicoConcluido> concluidosMes = filtrarPeriodo(
                concluidos,
                inicioMes,
                inicioProximoMes
        );
        List<OrcamentoRepository.DashboardServicoConcluido> concluidosMesAnterior = filtrarPeriodo(
                concluidos,
                inicioMesAnterior,
                inicioMes
        );
        List<OrcamentoRepository.DashboardServicoConcluido> concluidosSemana = filtrarPeriodo(
                concluidos,
                inicioSemana,
                inicioSemana.plusWeeks(1)
        );
        List<OrcamentoRepository.DashboardServicoConcluido> concluidosSemanaAnterior = filtrarPeriodo(
                concluidos,
                inicioSemanaAnterior,
                inicioSemana
        );

        BigDecimal ganhosMes = somarGanhos(concluidosMes);
        BigDecimal ganhosMesAnterior = somarGanhos(concluidosMesAnterior);
        BigDecimal ganhosSemana = somarGanhos(concluidosSemana);
        BigDecimal ganhosSemanaAnterior = somarGanhos(concluidosSemanaAnterior);
        Usuario usuarioProfissional = profissional.getUsuario();

        return new ProfissionalDashboardRS(
                profissional.getId(),
                usuarioProfissional.getNome(),
                Boolean.TRUE.equals(profissional.getStDisponivel()),
                valorOuZero(contadores.getOrcamentosPendentes()),
                valorOuZero(contadores.getServicosHoje()),
                new ProfissionalDashboardRS.GanhosMesRS(
                        ganhosMes,
                        ganhosMesAnterior,
                        calcularVariacao(ganhosMes, ganhosMesAnterior)
                ),
                new ProfissionalDashboardRS.ConcluidosMesRS(
                        concluidosMes.size(),
                        concluidosMesAnterior.size(),
                        calcularVariacao(concluidosMes.size(), concluidosMesAnterior.size())
                ),
                new ProfissionalDashboardRS.AvaliacaoRS(
                        usuarioProfissional.getQtAvaliacaoGeral(),
                        usuarioProfissional.getQtAvalicaoes() == null ? 0 : usuarioProfissional.getQtAvalicaoes(),
                        5
                ),
                calcularPercentual(
                        valorOuZero(contadores.getSolicitacoesRespondidasMes()),
                        valorOuZero(contadores.getSolicitacoesMes())
                ),
                new ProfissionalDashboardRS.GanhosSemanaRS(
                        ganhosSemana,
                        ganhosSemanaAnterior,
                        calcularVariacao(ganhosSemana, ganhosSemanaAnterior),
                        montarGanhosPorDia(concluidosSemana, inicioSemana)
                ),
                montarServicosMaisFeitos(concluidos)
        );
    }

    private List<OrcamentoRepository.DashboardServicoConcluido> filtrarPeriodo(List<OrcamentoRepository.DashboardServicoConcluido> concluidos, LocalDate inicio, LocalDate fimExclusivo) {
        LocalDateTime inicioPeriodo = inicio.atStartOfDay();
        LocalDateTime fimPeriodo = fimExclusivo.atStartOfDay();
        return concluidos.stream()
                .filter(item -> !item.getDataConclusao().isBefore(inicioPeriodo))
                .filter(item -> item.getDataConclusao().isBefore(fimPeriodo))
                .toList();
    }

    private List<ProfissionalDashboardRS.GanhoDiaRS> montarGanhosPorDia(
            List<OrcamentoRepository.DashboardServicoConcluido> concluidosSemana,
            LocalDate inicioSemana
    ) {
        Map<LocalDate, BigDecimal> ganhosPorDia = new HashMap<>();
        concluidosSemana.forEach(item -> ganhosPorDia.merge(
                item.getDataConclusao().toLocalDate(),
                valorMonetario(item.getValor()),
                BigDecimal::add
        ));

        List<ProfissionalDashboardRS.GanhoDiaRS> dias = new ArrayList<>(7);
        for (int indice = 0; indice < 7; indice++) {
            LocalDate data = inicioSemana.plusDays(indice);
            dias.add(new ProfissionalDashboardRS.GanhoDiaRS(
                    data,
                    abreviacao(data.getDayOfWeek()),
                    ganhosPorDia.getOrDefault(data, dinheiroZero()).setScale(2, RoundingMode.HALF_UP)
            ));
        }
        return List.copyOf(dias);
    }

    private List<ProfissionalDashboardRS.ServicoMaisFeitoRS> montarServicosMaisFeitos(List<OrcamentoRepository.DashboardServicoConcluido> concluidosMes) {
        Map<Long, ServicoAgrupado> servicos = new HashMap<>();
        concluidosMes.forEach(item -> servicos.compute(
                item.getCategoriaEspecificaId(), (id, atual) -> atual == null
                        ? new ServicoAgrupado(item.getCategoriaEspecifica(), 1)
                        : new ServicoAgrupado(atual.nome(), atual.quantidade() + 1)
        ));

        long maiorQuantidade = servicos.values().stream().mapToLong(ServicoAgrupado::quantidade).max().orElse(0);

        return servicos.entrySet().stream()
                .sorted(Comparator
                        .<Map.Entry<Long, ServicoAgrupado>>comparingLong(entry -> entry.getValue().quantidade())
                        .reversed()
                        .thenComparing(entry -> entry.getValue().nome()))
                .limit(3)
                .map(entry -> new ProfissionalDashboardRS.ServicoMaisFeitoRS(
                        entry.getKey(),
                        entry.getValue().nome(),
                        entry.getValue().quantidade(),
                        calcularPercentual(entry.getValue().quantidade(), maiorQuantidade)
                ))
                .toList();
    }

    private BigDecimal somarGanhos(List<OrcamentoRepository.DashboardServicoConcluido> concluidos) {
        return concluidos.stream()
                .map(item -> valorMonetario(item.getValor()))
                .reduce(dinheiroZero(), BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal valorMonetario(Double valor) {
        return valor == null ? dinheiroZero() : BigDecimal.valueOf(valor);
    }

    private BigDecimal dinheiroZero() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private Double calcularVariacao(BigDecimal atual, BigDecimal anterior) {
        if (anterior.signum() == 0) {
            return atual.signum() == 0 ? 0.0 : 100.0;
        }
        return atual.subtract(anterior)
                .multiply(BigDecimal.valueOf(100))
                .divide(anterior, 1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double calcularVariacao(long atual, long anterior) {
        if (anterior == 0) {
            return atual == 0 ? 0.0 : 100.0;
        }
        return arredondarUmaCasa(((atual - anterior) * 100.0) / anterior);
    }

    private Double calcularPercentual(long quantidade, long total) {
        if (total == 0) {
            return 0.0;
        }
        return arredondarUmaCasa((quantidade * 100.0) / total);
    }

    private Double arredondarUmaCasa(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    private long valorOuZero(Long valor) {
        return valor == null ? 0 : valor;
    }

    private String abreviacao(DayOfWeek diaSemana) {
        return switch (diaSemana) {
            case MONDAY -> "seg";
            case TUESDAY -> "ter";
            case WEDNESDAY -> "qua";
            case THURSDAY -> "qui";
            case FRIDAY -> "sex";
            case SATURDAY -> "sab";
            case SUNDAY -> "dom";
        };
    }

    private record ServicoAgrupado(String nome, long quantidade) {
    }
}
