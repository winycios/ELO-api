package br.com.elo.eloapi.model.publicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicacaoImagemRQ(
        @NotBlank String urlImagem,
        @NotNull Integer nrOrdem) {
}