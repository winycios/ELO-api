package br.com.elo.eloapi.service.search;

import br.com.elo.eloapi.model.search.ProfissionalSearchDocument;
import br.com.elo.eloapi.repository.ProfissionalRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfissionalSearchSyncService {

    private static final int TAMANHO_LOTE_REINDEXACAO = 100;

    private final ElasticsearchClient elasticsearchClient;
    private final ProfissionalSearchIndexManager indexManager;
    private final ProfissionalSearchDocumentLoader documentLoader;
    private final ProfissionalRepository profissionalRepository;

    public Set<Long> sincronizar(Collection<Long> profissionalIds) throws IOException {
        List<Long> ids = new ArrayList<>(new LinkedHashSet<>(profissionalIds));
        if (ids.isEmpty()) {
            return Set.of();
        }

        indexManager.garantirIndice();
        Map<Long, ProfissionalSearchDocument> documentos = documentLoader.carregar(ids);

        var bulk = new co.elastic.clients.elasticsearch.core.BulkRequest.Builder();
        for (Long profissionalId : ids) {
            ProfissionalSearchDocument documento = documentos.get(profissionalId);
            if (documento == null) {
                bulk.operations(operation -> operation.delete(delete -> delete.index(indexManager.getIndexName()).id(profissionalId.toString())));
            } else {
                bulk.operations(operation -> operation.index(index -> index.index(indexManager.getIndexName()).id(profissionalId.toString()).document(documento)));
            }
        }

        BulkResponse response = elasticsearchClient.bulk(bulk.build());
        Set<Long> sincronizados = new LinkedHashSet<>();
        for (int i = 0; i < response.items().size(); i++) {
            if (response.items().get(i).error() == null) {
                sincronizados.add(ids.get(i));
            }
        }
        return sincronizados;
    }

    public void reindexarTodos() throws IOException {
        List<Long> todosIds = profissionalRepository.findAllIds();
        for (int inicio = 0; inicio < todosIds.size(); inicio += TAMANHO_LOTE_REINDEXACAO) {
            int fim = Math.min(inicio + TAMANHO_LOTE_REINDEXACAO, todosIds.size());
            sincronizar(todosIds.subList(inicio, fim));
        }
    }
}
