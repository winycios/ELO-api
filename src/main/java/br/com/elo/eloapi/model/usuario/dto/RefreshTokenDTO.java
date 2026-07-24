package br.com.elo.eloapi.model.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(
    @NotBlank
    String refreshToken
) {
}
