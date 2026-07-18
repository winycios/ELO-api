package br.com.elo.eloapi.model.publicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicacaoCreateDTO {

    @NotNull
    private Long idCategoriaEspecifica;

    @Size(max = 200)
    @NotBlank
    private String dsPublicacao;

    @NotNull
    private List<PublicacaoImagemDTO> publicacaoImagemDTOList;
}

