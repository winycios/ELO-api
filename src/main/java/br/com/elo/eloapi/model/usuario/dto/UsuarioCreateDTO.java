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
public class UsuarioCreateDTO {

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
    @Size(min = 8, max = 20)
    private String senha;

    @NotNull
    private Boolean isDuplicarTel;

    @NotNull
    private CadastroAcao cadastroAcao;
}
