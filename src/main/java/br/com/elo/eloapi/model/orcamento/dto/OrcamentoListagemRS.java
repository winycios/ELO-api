package br.com.elo.eloapi.model.orcamento.dto;


public record OrcamentoListagemRS(
        Long id,
        Long idServico,
        Long idProfissional,
        Long orcamentoFinalId,
        String nomeProfissional,
        String fotoProfissional,
        String categoria,
        String descricao,
        String status
) {
}

