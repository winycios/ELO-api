package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ServicoCreateDTO(

        Long id,

        @NotNull Long idCategoriaEspecifica,

        @NotBlank String dsDescricao,

        @NotNull Double vlServico,

        @NotBlank String dsTag,

        @NotNull Integer tempoExperiencia,

        @NotBlank String tpExecucao,

        @NotNull List<ServicoDisponibilidadeCreateDTO> servicoDisponibilidadeCreateDTOList,

        @NotNull List<ServicoImagemCreateDTO> servicoImagemCreateDTOList
) {
}

