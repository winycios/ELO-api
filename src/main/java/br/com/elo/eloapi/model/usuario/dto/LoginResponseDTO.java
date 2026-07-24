package br.com.elo.eloapi.model.usuario.dto;

public record LoginResponseDTO(
        Long id,
        String token,
        String refreshToken,
        String nome,
        String urlPerfil,
        String urlPerfilPro,
        Boolean isProfissional,
        Boolean isCliente
) {
    public LoginResponseDTO(Long id, String token, String refreshToken) {
        this(id, token, refreshToken, null, null, null, null, null);
    }
}
