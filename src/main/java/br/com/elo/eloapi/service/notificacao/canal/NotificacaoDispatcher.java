package br.com.elo.eloapi.service.notificacao.canal;

import br.com.elo.eloapi.model.notificacao.CanalNotificacao;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificacaoDispatcher {

    private final Map<CanalNotificacao, CanalEnvioNotificacao> canais;

    public NotificacaoDispatcher(List<CanalEnvioNotificacao> implementacoes) {
        EnumMap<CanalNotificacao, CanalEnvioNotificacao> registrados = new EnumMap<>(CanalNotificacao.class);
        implementacoes.forEach(implementacao -> registrados.put(implementacao.getCanal(), implementacao));
        this.canais = Map.copyOf(registrados);
    }

    public ResultadoEnvioNotificacao enviar(ComandoEnvioNotificacao comando) {
        CanalEnvioNotificacao implementacao = canais.get(comando.canal());
        if (implementacao == null) {
            throw new FalhaTemporariaEnvioException("Canal temporariamente indisponível: " + comando.canal());
        }
        return implementacao.enviar(comando);
    }
}
