package br.com.elo.eloapi.model.orcamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record OrcamentoCreateRQ(

        @NotNull
        @Positive
        Long idServico,

        @NotBlank
        @Size(max = 100)
        String descricao,

        @Size(max = 3)
        List<@NotBlank @Size(max = 500) String> orcamentoImagemCreateRQList,

        @NotNull
        @Future
        LocalDateTime dtPreferidoSolicitado,

        @Positive
        Long idEndereco
) {
}
