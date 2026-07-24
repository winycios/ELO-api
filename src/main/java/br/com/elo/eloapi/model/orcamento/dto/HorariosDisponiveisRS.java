package br.com.elo.eloapi.model.orcamento.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record HorariosDisponiveisRS(
        Long idServico,
        LocalDate inicioSemana,
        LocalDate fimSemana,
        Integer intervaloMinutos,
        Integer margemAposReservaMinutos,
        List<DiaHorariosRS> dias
) {

    public record DiaHorariosRS(
            LocalDate data,
            Integer diaSemana,
            List<FaixaTrabalhoRS> expediente,
            List<LocalTime> horariosDisponiveis
    ) {
    }

    public record FaixaTrabalhoRS(
            LocalTime inicio,
            LocalTime fim
    ) {
    }
}
