package br.com.elo.eloapi.model.orcamento.dto;

public record OrcamentoResumoProfissionalRS(
        long pendentes,
        long aguardandoAprovacao,
        long aprovados,
        long emAndamento,
        long concluidos,
        long cancelados
) {
}
