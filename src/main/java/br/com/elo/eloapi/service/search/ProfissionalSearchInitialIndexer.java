package br.com.elo.eloapi.service.search;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfissionalSearchInitialIndexer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfissionalSearchInitialIndexer.class);

    private final ProfissionalSearchSyncService syncService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            syncService.reindexarTodos();
            LOGGER.info("Carga inicial do indice de profissionais concluida.");
        } catch (Exception exception) {
            LOGGER.warn("Nao foi possivel executar a carga inicial do Elasticsearch. A aplicacao continuara ativa.",
                    exception);
        }
    }
}
