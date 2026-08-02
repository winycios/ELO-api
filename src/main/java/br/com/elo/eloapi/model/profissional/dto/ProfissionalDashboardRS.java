package br.com.elo.eloapi.model.profissional.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProfissionalDashboardRS(
        Long profissionalId,
        String nome,
        Boolean modoProfissionalAtivo,
        long novosOrcamentos,
        long servicosHoje,
        GanhosMesRS ganhosMes,
        ConcluidosMesRS concluidosMes,
        AvaliacaoRS avaliacao,
        Double taxaRespostaPercentual,
        GanhosSemanaRS ganhosSemana,
        List<ServicoMaisFeitoRS> servicosMaisFeitos
) {

    public record GanhosMesRS(
            BigDecimal valor,
            BigDecimal valorMesAnterior,
            Double variacaoPercentual
    ) {
    }

    public record ConcluidosMesRS(
            long quantidade,
            long quantidadeMesAnterior,
            Double variacaoPercentual
    ) {
    }

    public record AvaliacaoRS(
            Double media,
            int quantidade,
            int notaMaxima
    ) {
    }

    public record GanhosSemanaRS(
            BigDecimal valor,
            BigDecimal valorSemanaAnterior,
            Double variacaoPercentual,
            List<GanhoDiaRS> dias
    ) {
    }

    public record GanhoDiaRS(
            LocalDate data,
            String diaSemana,
            BigDecimal valor
    ) {
    }

    public record ServicoMaisFeitoRS(
            Long categoriaEspecificaId,
            String nome,
            long quantidade,
            Double percentualDoMaisFeito
    ) {
    }
}
