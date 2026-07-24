package br.com.elo.eloapi.model.publicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PublicacaoCreateDTO(
        @NotNull Long idCategoriaEspecifica,
        @Size(max = 200) @NotBlank String dsPublicacao,
        @NotNull List<PublicacaoImagemDTO> publicacaoImagemDTOList
) {
}