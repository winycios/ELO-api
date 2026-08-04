package br.com.elo.eloapi.model.publicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PublicacaoImagemRQ(
        @NotBlank @Size(max = 500) String chaveImagem,
        @NotNull Integer nrOrdem) {
}
