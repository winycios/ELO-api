package br.com.elo.eloapi.implementation;

import br.com.elo.eloapi.model.notificacao.CanalNotificacao;
import br.com.elo.eloapi.model.notificacao.dto.ComandoEnvioNotificacao;
import br.com.elo.eloapi.model.notificacao.dto.ResultadoEnvioNotificacao;

public interface CanalEnvioNotificacao {

    CanalNotificacao getCanal();

    ResultadoEnvioNotificacao enviar(ComandoEnvioNotificacao comando);
}
