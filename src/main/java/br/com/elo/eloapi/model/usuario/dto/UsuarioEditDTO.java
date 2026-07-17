package br.com.elo.eloapi.model.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEditDTO {

    private Long id;

    @NotBlank
    @Size(min = 3)
    private String nome;

    @NotBlank
    private String sobrenome;

    @Email
    private String email;

    @NotBlank
    private String telContato;

    @NotBlank
    private String telContatoZap;
}
