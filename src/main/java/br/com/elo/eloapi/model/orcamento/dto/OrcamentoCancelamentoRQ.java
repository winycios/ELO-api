package br.com.elo.eloapi.model.orcamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrcamentoCancelamentoRQ(
        @NotBlank
        @Size(max = 50)
        String motivo,

        @NotBlank
        @Size(max = 200)
        String descricao
) {
}
