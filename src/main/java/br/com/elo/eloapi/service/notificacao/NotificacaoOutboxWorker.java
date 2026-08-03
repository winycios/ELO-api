package br.com.elo.eloapi.service.notificacao;

import br.com.elo.eloapi.service.notificacao.canal.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificacaoOutboxWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoOutboxWorker.class);

    private final NotificacaoOutboxService outboxService;
    private final NotificacaoDispatcher dispatcher;

    @Value("${elo.notification.outbox.max-attempts:10}")
    private Integer maxTentativas;

    @Scheduled(fixedDelayString = "${elo.notification.outbox.fixed-delay-ms:3000}", initialDelayString = "${elo.notification.outbox.initial-delay-ms:5000}")
    public void processar() {
        List<Long> entregas = outboxService.reservarPendentes(maxTentativas);
        for (Long entregaId : entregas) {
            processarEntrega(entregaId);
        }
    }

    private void processarEntrega(Long entregaId) {
        try {
            ComandoEnvioNotificacao comando = outboxService.prepararEnvio(entregaId);
            ResultadoEnvioNotificacao resultado = dispatcher.enviar(comando);
            outboxService.concluir(entregaId, resultado.codigoMensagemProvedor());
        } catch (FalhaPermanenteEnvioException exception) {
            outboxService.registrarFalhaPermanente(entregaId, exception);
            LOGGER.warn("Falha permanente na entrega de notificação {}: {}", entregaId, exception.getMessage());
        } catch (FalhaTemporariaEnvioException exception) {
            outboxService.registrarFalhaTemporaria(entregaId, exception, maxTentativas);
            LOGGER.warn("Falha temporária na entrega de notificação {}: {}", entregaId, exception.getMessage());
        } catch (Exception exception) {
            outboxService.registrarFalhaTemporaria(entregaId, exception, maxTentativas);
            LOGGER.error("Falha inesperada na entrega de notificação {}.", entregaId, exception);
        }
    }
}
