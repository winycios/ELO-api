package br.com.elo.eloapi.model.publicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioCreateRQ(
        @NotBlank @Size(max = 200) String texto,
        Long comentarioPaiId
) {
}