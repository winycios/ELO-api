package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicoDisponibilidadeCreateDTO {

    private Long id;

    @NotNull
    private Integer diaSemana;

    @NotNull
    private LocalTime hrInicio;

    @NotNull
    private LocalTime hrFim;
}
