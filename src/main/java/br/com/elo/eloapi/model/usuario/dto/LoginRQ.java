package br.com.elo.eloapi.model.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRQ(
    @NotBlank
    @Email
    String email,

    @NotBlank
    String senha,

    @NotBlank
    String deviceCode
) {
}
