package br.com.elo.eloapi.model.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioEditRQ(
        Long id,

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
        String telContatoZap
) {
}
