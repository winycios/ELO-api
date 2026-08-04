package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServicoImagemCreateRQ(

        Long id,

        @NotBlank
        @Size(max = 500)
        String chaveImagem,

        @NotNull
        Integer ordem
        ) {
}
