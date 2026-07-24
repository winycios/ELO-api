package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ServicoCreateRQ(

        Long id,

        @NotNull Long idCategoriaEspecifica,

        @NotBlank String dsDescricao,

        @NotNull Double vlServico,

        @NotBlank String dsTag,

        @NotNull Integer tempoExperiencia,

        @NotBlank String tpExecucao,

        @NotEmpty List<@Valid ServicoDisponibilidadeCreateRQ> servicoDisponibilidadeCreateRQList,

        @NotNull List<@Valid ServicoImagemCreateRQ> servicoImagemCreateRQList
) {
}

