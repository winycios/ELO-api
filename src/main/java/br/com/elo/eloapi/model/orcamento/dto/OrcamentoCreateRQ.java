package br.com.elo.eloapi.model.orcamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record OrcamentoCreateRQ(

        @NotNull Long idServico,
        @NotBlank String descricao,
        @NotNull List<String> orcamentoImagemCreateRQList,
        @NotNull LocalDateTime dtPreferidoSolicitado,
        @NotNull Long idEndereco
) {
}
