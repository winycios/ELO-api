package br.com.elo.eloapi.model.publicacao.dto;

import java.time.LocalDateTime;

public record ComentarioRS(
        Long id,
        String texto,
        LocalDateTime comentadoEm,
        Long comentarioPaiId,
        Long usuarioId,
        String usuarioNome,
        String usuarioFotoUrl
) {
}
