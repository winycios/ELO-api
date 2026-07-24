package br.com.elo.eloapi.model.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(
        @NotBlank
        @Size(min = 3)
        String nome,

        @NotBlank
        String sobrenome,

        @Email
        String email,

        @NotBlank
        String telContato,

        @NotBlank
        @Size(min = 8, max = 20)
        String senha,

        @NotNull
        Boolean isDuplicarTel,

        @NotNull
        CadastroAcao cadastroAcao
) {
}
