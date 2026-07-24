package br.com.elo.eloapi.model.publicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicacaoImagemDTO(
        @NotBlank String urlImagem,
        @NotNull Integer nrOrdem) {
}