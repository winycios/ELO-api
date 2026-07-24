package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ServicoDisponibilidadeCreateRQ(

        Long id,

        @NotNull
        @Min(1)
        @Max(7)
        Integer diaSemana,

        @NotNull
        LocalTime hrInicio,

        @NotNull
        LocalTime hrFim
) {
}
