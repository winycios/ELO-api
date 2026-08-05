package br.com.elo.eloapi.model.servico.dto;

import java.time.LocalTime;
import java.util.List;

public record ServicoRS(Long id,
                        Long idProfissional,
                        ServicoCategoriaRS servicoCategoriaRS,
                        List<ServicoImagemRS> servicoImagemRSList,
                        List<ServicoDisponibilidadeRS> servicoDisponibilidadeRSList,
                        String dsDescricao,
                        Double vlServico,
                        Integer tempoExperiencia,
                        String dsTag,
                        String tpExecucao) {

    public record ServicoCategoriaRS(Long idCategoriaEspecifica, Long idCategoriaGeral) {
    }

    public record ServicoImagemRS(Long idServicoImagem, String chave, String url, Integer ordem) {
    }

    public record ServicoDisponibilidadeRS(Long idServicoDisponibilidade,
                                           Integer diaSemana,
                                           LocalTime hrInicio,
                                           LocalTime hrFim) {
    }
}
