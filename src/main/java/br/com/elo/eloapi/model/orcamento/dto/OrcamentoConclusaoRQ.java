package br.com.elo.eloapi.model.orcamento.dto;

import jakarta.validation.constraints.Size;

public record OrcamentoConclusaoRQ(
        @Size(max = 200)
        String observacao
) {
}
