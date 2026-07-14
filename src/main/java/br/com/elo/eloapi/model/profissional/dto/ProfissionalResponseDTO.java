package br.com.elo.eloapi.model.profissional.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfissionalResponseDTO {

    private Long usuarioId;
    private LocalDateTime dtCriacao;
    private Integer qtServicos;
    private Integer qtRespostaGeral;
    private Boolean disponivel;
    private String apresentacao;
    private String uriPerfil;
    private String especialidades;
}
