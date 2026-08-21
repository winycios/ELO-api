package br.com.elo.eloapi.service.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;

@Component
@RequiredArgsConstructor
public class ProfissionalSearchIndexManager {

    private static final String INDEX_DEFINITION = """
            {
              "settings": {
                "number_of_shards": 1,
                "number_of_replicas": 0,
                "analysis": {
                  "filter": {
                    "elo_asciifolding": {
                      "type": "asciifolding",
                      "preserve_original": true
                    }
                  },
                  "analyzer": {
                    "elo_text": {
                      "type": "custom",
                      "tokenizer": "standard",
                      "filter": ["lowercase", "elo_asciifolding"]
                    }
                  }
                }
              },
              "mappings": {
                "dynamic": "strict",
                "properties": {
                  "profissionalId": { "type": "long" },
                  "nome": { "type": "text", "analyzer": "elo_text" },
                  "fotoPerfil": { "type": "keyword", "index": false },
                  "habilitado": { "type": "boolean" },
                  "disponivel": { "type": "boolean" },
                  "avaliacao": { "type": "double" },
                  "quantidadeAvaliacoes": { "type": "integer" },
                  "servicosConcluidos": { "type": "integer" },
                  "localizacao": { "type": "geo_point" },
                  "raioAtendimentoKm": { "type": "integer" },
                  "cidade": { "type": "keyword" },
                  "estado": { "type": "keyword" },
                  "bairro": { "type": "keyword" },
                  "reputacaoPln": {
                    "type": "object",
                    "dynamic": "strict",
                    "properties": {
                      "comentariosProcessados": { "type": "integer" },
                      "percentualPositivo": { "type": "float" },
                      "percentualNeutro": { "type": "float" },
                      "percentualNegativo": { "type": "float" },
                      "sentimentoMedio": { "type": "float" },
                      "taxaInconsistencia": { "type": "float" },
                      "pontosFortes": { "type": "keyword" },
                      "pontosFracos": { "type": "keyword" },
                      "resumo": { "type": "text", "index": false },
                      "versaoModelo": { "type": "keyword" },
                      "dataAtualizacao": { "type": "date" }
                    }
                  },
                  "servicos": {
                    "type": "nested",
                    "properties": {
                      "servicoId": { "type": "long" },
                      "categoriaGeralId": { "type": "long" },
                      "categoriaGeral": { "type": "text", "analyzer": "elo_text" },
                      "categoriaEspecificaId": { "type": "long" },
                      "categoriaEspecifica": { "type": "text", "analyzer": "elo_text" },
                      "descricao": { "type": "text", "analyzer": "elo_text" },
                      "tags": { "type": "text", "analyzer": "elo_text" },
                      "preco": { "type": "double" },
                      "tipoExecucao": { "type": "keyword" },
                      "tempoExperiencia": { "type": "integer" },
                      "ativo": { "type": "boolean" }
                    }
                  }
                }
              }
            }
            """;

    private final ElasticsearchClient elasticsearchClient;

    @Getter
    @Value("${elo.search.index-name:profissionais_search_v1}")
    private String indexName;

    private volatile boolean verificado;

    public synchronized void garantirIndice() throws IOException {
        if (verificado) {
            return;
        }

        boolean existe = elasticsearchClient.indices()
                .exists(request -> request.index(indexName))
                .value();

        if (!existe) {
            elasticsearchClient.indices().create(request -> request
                    .index(indexName)
                    .withJson(new StringReader(INDEX_DEFINITION)));
        }
        verificado = true;
    }

}
