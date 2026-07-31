package br.com.elo.eloapi.model.avaliacao.dto;

import java.time.LocalDateTime;

public record AvaliacaoOrcamentoRS(
        Long id,
        Long idOrcamento,
        Long idAvaliador,
        Long idUsuarioAvaliado,
        Integer nota,
        String comentario,
        LocalDateTime data
) {
}
