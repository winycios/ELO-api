package br.com.elo.eloapi.model.usuario.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String token;
    private String refreshToken;
    private String nome;
    private String urlPerfil;
    private String urlPerfilPro;
    private Boolean isProfissional;
    private Boolean isCliente;

    public LoginResponseDTO(Long id, String token, String refreshToken) {
        this.id = id;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
