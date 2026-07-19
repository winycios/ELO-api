package br.com.elo.eloapi.model.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoCreateDTO {

    private Long id;

    @NotBlank
    @Size(min = 3)
    private String nmApelido;

    @NotBlank
    private String tipoEndereco;

    @NotBlank
    private String cep;

    @NotBlank
    private String rua;

    @NotNull
    private Integer nrRua;

    private String complemento;

    @NotBlank
    private String bairro;

    @NotBlank
    private String cidade;

    @NotBlank
    private String estado;
}
