package br.com.elo.eloapi.model.servico.dto;

public record ServicoListaRS(Long id,
                             ServicoRS.ServicoCategoriaRS categoria,
                             String dsDescricao,
                             Double vlServico,
                             String dsTag,
                             String tpExecucao) {
}
