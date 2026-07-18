package br.com.elo.eloapi.model.publicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicacaoImagemDTO {

    @NotBlank
    private String urlImagem;

    @NotNull
    private Integer nrOrdem;
}
