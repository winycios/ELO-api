package br.com.elo.eloapi.model.orcamento.dto;

import co.elastic.clients.util.DateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrcamentoCreateRQ(

        @NotNull Long idServico,
        @NotBlank String descricao,
        @NotNull List<OrcamentoImagemCreateRQ> orcamentoImagemCreateRQList,
        @NotNull DateTime dtEscolhida,
        @NotNull Long idEndereco
) {


    public record OrcamentoImagemCreateRQ(
            @NotBlank String url
    ) {
    }

}
