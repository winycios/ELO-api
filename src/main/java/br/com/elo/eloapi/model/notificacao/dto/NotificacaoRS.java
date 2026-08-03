package br.com.elo.eloapi.model.notificacao.dto;

import br.com.elo.eloapi.model.notificacao.Notificacao;
import br.com.elo.eloapi.model.notificacao.TipoNotificacao;

import java.time.LocalDateTime;

public record NotificacaoRS(
        Long id,
        TipoNotificacao tipo,
        Long orcamentoId,
        String titulo,
        String mensagem,
        LocalDateTime criadoEm,
        LocalDateTime lidoEm
) {
    public static NotificacaoRS from(Notificacao notificacao) {
        return new NotificacaoRS(
                notificacao.getId(),
                notificacao.getTipo(),
                notificacao.getOrcamento().getId(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getDtCriacao(),
                notificacao.getDtLeitura()
        );
    }
}
