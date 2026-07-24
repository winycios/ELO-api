package br.com.elo.eloapi.model.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnderecoCreateRQ(

        Long id,

        @NotBlank
        @Size(min = 3) String nmApelido,

        @NotBlank String tipoEndereco,

        @NotBlank String cep,

        @NotBlank String rua,

        @NotNull Integer nrRua,

        String complemento,

        @NotBlank String bairro,

        @NotBlank String cidade,

        @NotBlank String estado
) {
}
