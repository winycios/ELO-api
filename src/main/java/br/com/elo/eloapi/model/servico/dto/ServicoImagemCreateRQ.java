package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServicoImagemCreateRQ(

        Long id,

        @NotBlank
        String url,

        @NotNull
        Integer ordem
        ) {
}
