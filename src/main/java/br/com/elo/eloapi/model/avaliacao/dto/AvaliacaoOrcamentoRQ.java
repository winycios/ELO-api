package br.com.elo.eloapi.model.avaliacao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoOrcamentoRQ(
        @NotNull
        @Min(1)
        @Max(5)
        Integer nota,

        @Size(max = 200)
        String comentario
) {
}
