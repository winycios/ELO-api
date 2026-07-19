package br.com.elo.eloapi.service.search;

import br.com.elo.eloapi.model.search.SearchOutbox;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SearchOutboxWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchOutboxWorker.class);

    private final SearchOutboxService outboxService;
    private final ProfissionalSearchSyncService syncService;

    @Value("${elo.search.outbox.max-attempts:10}")
    private Integer maxTentativas;

    @Scheduled(
            fixedDelayString = "${elo.search.outbox.fixed-delay-ms:300000}",
            initialDelayString = "${elo.search.outbox.initial-delay-ms:300000}"
    )
    public void processar() {
        List<SearchOutbox> eventos = outboxService.buscarPendentes(maxTentativas);
        if (eventos.isEmpty()) {
            return;
        }

        List<Long> profissionalIds = eventos.stream()
                .map(SearchOutbox::getProfissionalId)
                .distinct()
                .toList();

        try {
            Set<Long> sincronizados = syncService.sincronizar(profissionalIds);
            outboxService.concluirProcessamento(eventos, sincronizados);
            LOGGER.info("Outbox Elasticsearch: {} de {} profissionais sincronizados.",
                    sincronizados.size(), profissionalIds.size());
        } catch (Exception exception) {
            outboxService.registrarFalha(eventos);
            LOGGER.error("Falha ao processar {} eventos da outbox do Elasticsearch.", eventos.size(), exception);
        }
    }
}
