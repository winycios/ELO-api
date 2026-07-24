package br.com.elo.eloapi.model.areaAtendimento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AreaAtendimentoUpdateDTO(
        Long id,

        @NotNull Double nrLatitude,

        @NotNull Double nrLongitude,

        @NotNull Integer nrRaio,

        @NotBlank String nmCidade,

        @NotBlank String nmEstado,

        @NotBlank String nmBairro
) {
}

