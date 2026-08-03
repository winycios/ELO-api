package br.com.elo.eloapi.service.notificacao.canal;

import br.com.elo.eloapi.model.notificacao.CanalNotificacao;

public interface CanalEnvioNotificacao {

    CanalNotificacao getCanal();

    ResultadoEnvioNotificacao enviar(ComandoEnvioNotificacao comando);
}
