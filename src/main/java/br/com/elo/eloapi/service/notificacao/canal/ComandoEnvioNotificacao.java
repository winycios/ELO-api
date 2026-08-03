package br.com.elo.eloapi.service.notificacao.canal;

import br.com.elo.eloapi.model.notificacao.CanalNotificacao;
import br.com.elo.eloapi.model.notificacao.TipoIdentificadorFcm;
import br.com.elo.eloapi.model.notificacao.TipoNotificacao;

import java.util.Map;

public record ComandoEnvioNotificacao(
        Long outboxId,
        CanalNotificacao canal,
        Long destinatarioId,
        String email,
        Long dispositivoId,
        String identificadorFcm,
        TipoIdentificadorFcm tipoIdentificadorFcm,
        TipoNotificacao tipo,
        Long orcamentoId,
        Long notificacaoId,
        String titulo,
        String mensagem,
        Map<String, String> dados
) {
}
