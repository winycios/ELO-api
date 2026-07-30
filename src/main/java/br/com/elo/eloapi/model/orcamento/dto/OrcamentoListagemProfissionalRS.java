package br.com.elo.eloapi.model.orcamento.dto;

import java.time.LocalDateTime;

public record OrcamentoListagemProfissionalRS(
        Long id,
        Long idServico,
        String nomeUsuario,
        String fotoUsuario,
        Double avaliacaoUsuario,
        String categoria,
        String descricao,
        Double distanciaKm,
        LocalDateTime dataHoraCriacao,
        String status
) {
}
