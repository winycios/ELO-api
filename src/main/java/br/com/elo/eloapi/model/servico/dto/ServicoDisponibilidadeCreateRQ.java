package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ServicoDisponibilidadeCreateRQ(

        Long id,

        @NotNull
        Integer diaSemana,

        @NotNull
        LocalTime hrInicio,

        @NotNull
        LocalTime hrFim
) {
}
