# Carga de dados do Elo

Como popular o banco `database_elo` — automaticamente ao subir o container, ou
manualmente num banco que já existe — e como alimentar o módulo de PLN
(`ELO-PLN`) com esses dados.

---

## 1. Os arquivos, na ordem

Todos ficam em `src/main/resources/db/`. **A ordem importa**: cada um depende
dos anteriores.

| # | Arquivo | O que cria | Depende de |
|---|---|---|---|
| 1 | `script.sql` | as 25 tabelas do schema | — |
| 2 | `categorias_seed.sql` | 40 categorias gerais e 61 específicas | 1 |
| 3 | `orcamento_status_seed.sql` | os 5 status de orçamento | 1 |
| 4 | `users_seed.sql` | usuários 1–3 e seus perfis profissionais | 1 |
| 5 | `servicos_seed.sql` | 6 serviços dos profissionais 1 e 3, com disponibilidade e imagens | 2, 4 |
| 6 | `vitrine_seed.sql` | 40 publicações da vitrine | 2, 4 |
| 7 | `avaliacoes_seed.sql` | 14 profissionais, 36 clientes, 37 serviços, 320 orçamentos e 340 avaliações | 2, 3 |

O passo 3 não é opcional: `OrcamentoService` lança
`IllegalStateException("O status ... não está configurado")` se a tabela
`orcamento_status` estiver vazia.

Estado final do banco depois dos sete:

```
usuarios       53      orcamentos                320
servicos       43      avaliacoes                340
publicacoes    40      avaliacoes com comentario 328
```

Dessas 340 avaliações, **308 são visíveis ao PLN** — são as em que o usuário
avaliado é o profissional do serviço contratado. As outras 20 são o profissional
avaliando o cliente, e o `SELECT` do módulo as ignora de propósito.

---

## 2. Subindo do zero (o caminho automático)

O `docker-compose.yml` monta os sete arquivos em
`/docker-entrypoint-initdb.d/`, numerados de `01` a `07`. O container executa
todos em ordem alfabética no primeiro boot:

```bash
docker compose up -d mysql
```

Espere a inicialização terminar (uns 40 segundos na primeira vez) e confira:

```bash
docker exec elo-mysql mysql -uroot -p1234 -D database_elo \
  -e "SELECT COUNT(*) FROM avaliacao_reserva;"
```

Deve devolver `340`.

> ⚠️ **O `docker-entrypoint-initdb.d` só roda quando o volume `mysql-data`
> está vazio.** Num banco que já existe ele é simplesmente ignorado, sem aviso
> nenhum. Se você adicionou um seed novo e nada aconteceu, é isso — use o
> caminho manual da seção 3 ou recrie o volume (seção 4).

---

## 3. Aplicando num banco que já existe (o caminho manual)

```bash
cd src/main/resources/db

for f in script.sql categorias_seed.sql orcamento_status_seed.sql \
         users_seed.sql servicos_seed.sql vitrine_seed.sql avaliacoes_seed.sql; do
  echo "aplicando $f"
  docker exec -i elo-mysql mysql -uroot -p1234 -D database_elo \
    --default-character-set=utf8mb4 < "$f"
done
```

No PowerShell:

```powershell
cd src\main\resources\db
$arquivos = 'script.sql','categorias_seed.sql','orcamento_status_seed.sql',
            'users_seed.sql','servicos_seed.sql','vitrine_seed.sql','avaliacoes_seed.sql'
foreach ($f in $arquivos) {
    Write-Host "aplicando $f"
    Get-Content $f -Raw -Encoding utf8 |
        docker exec -i elo-mysql mysql -uroot -p1234 -D database_elo --default-character-set=utf8mb4
}
```

Os arquivos do 2 ao 7 são **re-executáveis**: `script.sql` usa
`CREATE TABLE IF NOT EXISTS`, os seeds intermediários usam `WHERE NOT EXISTS`, e
`avaliacoes_seed.sql` apaga a própria faixa de ids antes de inserir. Rodar duas
vezes não duplica nada — está testado.

### Sobre acentuação

Todo seed começa com `SET NAMES utf8mb4;`. **Não remova essa linha.** O
entrypoint do container MySQL importa os arquivos sem definir o charset do
cliente; sem o `SET NAMES`, os bytes UTF-8 são lidos como latin1 e
`Instalação elétrica` vira `InstalaÃ§Ã£o elÃ©trica` no banco. Pior: o join do
`avaliacoes_seed.sql` casa mesmo assim (os dois lados ficam corrompidos igual),
então o erro passa despercebido até alguém olhar a tela.

---

## 4. Recomeçar do zero

```bash
docker compose down mysql
docker volume rm elo-api_mysql-data
docker compose up -d mysql
```

Isso apaga **todos** os dados e reexecuta os sete arquivos na subida.

Para apagar só os dados do `avaliacoes_seed.sql` sem derrubar o resto, basta
reaplicá-lo — o bloco de limpeza no topo do arquivo remove a faixa reservada
respeitando a ordem das chaves estrangeiras.

---

## 5. Faixas de id reservadas

O `avaliacoes_seed.sql` é dono destas faixas. Não use esses ids em outros seeds:

| Faixa | Conteúdo |
|---|---|
| `usuario` 101–114 | os 14 profissionais |
| `usuario` 201–236 | os 36 clientes |
| `orcamento` 1001–1320 | os orçamentos concluídos |

Os ids 1–3 de `usuario` continuam sendo do `users_seed.sql`.

**Login**: todos os usuários do seed usam o mesmo hash bcrypt do
`users_seed.sql` — a senha é a mesma do `profissional@gmail.com`.

---

## 6. O que tem nas avaliações

Os 308 comentários visíveis ao PLN foram escritos um a um, sem template, para
exercitar o pipeline inteiro. Cada profissional tem uma reputação própria
(média de 2,05 a 4,62), então a agregação produz pontos fortes e fracos
diferentes por perfil em vez de todo mundo igual.

| Cobertura | Exemplo |
|---|---|
| Negação | `não atrasou`, `não recomendo`, `nunca mais contrato` |
| Expressões fixas | `sem dúvida`, `nada a reclamar`, `sem falta`, `nem sempre` |
| Adversativas (os NEUTRO difíceis) | `atrasou, mas avisou antes` |
| Gíria e erro de digitação | `otimo trabalho, recomendo dms! vlw`, `ficou otimoooo` |
| Caixa alta | `NÃO RECOMENDO. TRABALHO MAL FEITO` |
| Nota contradiz o texto | nota 2 com texto elogioso, nota 5 com reclamação |
| Dado pessoal | telefone, e-mail e CPF, para a anonimização trocar por `<TELEFONE>`, `<EMAIL>`, `<DOC>` |
| Curtos demais | `ok`, `:(` — passam no SQL e são descartados pelo filtro do Python |

Distribuição pelo rótulo fraco da nota: **62% POSITIVO, 15% NEUTRO,
23% NEGATIVO** — assimetria realista de marketplace, mas com classe minoritária
suficiente para a macro-F1 significar alguma coisa.

O texto das avaliações é ASCII de propósito (só os nomes de categoria vão
acentuados, porque precisam casar com o `categorias_seed.sql`). O PLN roda
`remover_acentos()` antes de gerar característica, então acento no comentário
não muda nada para o modelo e só aumentaria o risco de mojibake na importação.

---

## 7. Alimentando o módulo de PLN

Com o banco populado, o `ELO-PLN` lê direto dele. Todas as configurações são
variáveis de ambiente.

```powershell
cd C:\Users\User\PycharmProjects\ELO-PLN
.\.venv\Scripts\activate

$env:ELO_PLN_REPOSITORIO = "mysql"
$env:ELO_DB_HOST     = "localhost"
$env:ELO_DB_PORT     = "3306"
$env:ELO_DB_NAME     = "database_elo"
$env:ELO_DB_USER     = "winyc"
$env:ELO_DB_PASSWORD = "1234"
```

Não existe carregamento de `.env` no `config.py` — as variáveis valem só na
sessão atual do terminal. Para fixar:
`[Environment]::SetEnvironmentVariable('ELO_PLN_REPOSITORIO','mysql','User')`.

### 7.1 Só rodar o worker (modelo já treinado)

O worker **não treina**: ele carrega o artefato de
`models/{ELO_PLN_VERSAO_MODELO}-baseline` e usa.

```powershell
elo-pln info      # confira artefatoPresente: true e repositorio: mysql
elo-pln worker    # uma rodada; use --continuo para o loop de 60s
```

Ele classifica os pendentes, grava em `avaliacao_analise_pln`, agrega
`profissional_reputacao_pln` e enfileira os profissionais na `search_outbox`.

Das 308 avaliações, **306 são processadas** — as 2 que sobram são o `ok` e o
`:(`, descartados por `comentario_utilizavel` (mínimo de 3 caracteres e ao menos
uma letra). Elas ficam pendentes para sempre; estão no fim da fila de propósito,
para não travar o início do lote.

### 7.2 Treinar com os dados do banco

```powershell
$env:ELO_PLN_VERSAO_MODELO = "sentimento-ptbr-v2"

elo-pln extrair                        # 308 avaliacoes -> data/raw (ja anonimizadas)
elo-pln preparar                       # 306 exemplos -> treino 216 / validacao 46 / teste 44
elo-pln exportar-revisao --tamanho 200 # revise rotulo_revisado no CSV
elo-pln aplicar-revisao
elo-pln treinar-baseline
elo-pln avaliar --split teste
elo-pln worker                         # reprocessa tudo com a versao nova
```

⚠️ `elo-pln extrair` **sobrescreve** `data/raw/avaliacoes.jsonl` e `preparar`
sobrescreve `data/splits/`. Faça backup antes se quiser guardar um dataset
anterior.

Trocar `ELO_PLN_VERSAO_MODELO` cria um artefato novo em vez de sobrescrever o
antigo, e faz o worker reprocessar todas as avaliações (a coluna
`cd_versao_modelo` participa da consulta de pendentes). É o jeito certo de
comparar dois modelos no mesmo banco.

### 7.3 Resultado do treino com estes dados

| Modelo | Treinado em | Acurácia no teste | macro-F1 |
|---|---|---|---|
| `sentimento-ptbr-v1` | dataset sintético | 82,8% (no teste sintético) | 0,825 |
| `sentimento-ptbr-v2` | 306 comentários deste seed | **75,0%** | **0,644** |

Por classe na v2: POSITIVO F1 0,86 · NEUTRO 0,57 · NEGATIVO 0,50. O recall de
NEGATIVO é 0,40 — metade dos negativos ainda escapa como POSITIVO. As faixas de
confiança separam bem: acima de 0,70 a acurácia é 96%; abaixo de 0,55 cai para
33%. Ou seja, o limiar já serve como triagem para revisão manual.

O teste tem só 44 exemplos (6 deles NEUTRO), então esses números têm margem de
erro grande. E a etapa de revisão manual (`exportar-revisao` / `aplicar-revisao`)
ainda **não foi feita** — os rótulos vêm todos da nota. Antes de reportar
qualquer coisa como resultado, revise o teste.

Comparando as duas versões sobre os mesmos 306 comentários reais, a
concordância com o rótulo da nota foi de **66,3% (v1) para 92,8% (v2)** — mas
216 desses comentários estavam no treino da v2, então o número honesto continua
sendo os 75% do conjunto de teste.

---

## 8. Verificação rápida

```sql
-- avaliacoes que o PLN enxerga
SELECT COUNT(*)
  FROM avaliacao_reserva a
  JOIN orcamento o    ON o.id_orcamento = a.fk_id_reserva
  JOIN servico s      ON s.id_servico   = o.fk_id_servico
  JOIN profissional p ON p.usuario_id   = s.fk_id_profissional_usuario
 WHERE a.fk_id_usuario_avaliado = p.usuario_id
   AND TRIM(COALESCE(a.ds_comentario, '')) <> '';   -- esperado: 308

-- o que o PLN produziu
SELECT tp_sentimento, COUNT(*) FROM avaliacao_analise_pln GROUP BY tp_sentimento;

SELECT u.nm_nome, r.nr_percentual_positivo, r.js_pontos_fortes, r.js_pontos_fracos, r.ds_resumo
  FROM profissional_reputacao_pln r
  JOIN usuario u ON u.id_usuario = r.fk_id_profissional
 ORDER BY r.nr_sentimento_medio DESC;

-- acentuacao intacta? deve sair "Instalação elétrica"
SELECT nm_categoria_especifica FROM categoria_especifica LIMIT 1;
```

---

## 9. O ciclo completo, do comentário à busca

```
worker do PLN ──► avaliacao_analise_pln      (uma linha por comentário)
              ──► profissional_reputacao_pln (agregado por profissional)
              ──► search_outbox              (pedido de reindexação)
                        │
                        ▼
          SearchOutboxWorker (Java, @Scheduled a cada 5 min)
                        │
                        ▼
          ProfissionalSearchDocumentLoader ──► monta o documento,
                                               incluindo reputacaoPln
                        │
                        ▼
                 bulk index no Elasticsearch
                        │
                        ▼
          GET /api/busca/profissionais devolve o campo `reputacao`
```

O `SearchOutboxWorker` tem `initialDelay` de 5 minutos, então nada acontece nos
primeiros 5 minutos após a API subir. Se quiser ver antes, reinicie a aplicação:
o `ProfissionalSearchInitialIndexer` reindexa todo mundo no boot.

A busca devolve a reputação assim:

```json
{
  "nome": "Marcos Vieira",
  "avaliacao": 4.6,
  "reputacao": {
    "comentariosProcessados": 23,
    "percentualPositivo": 86.96,
    "sentimentoMedio": 0.8913,
    "pontosFortes": ["QUALIDADE", "PONTUALIDADE", "PRECO"],
    "pontosFracos": ["RAPIDEZ"],
    "resumo": "Clientes destacam o trabalho pela qualidade do serviço, pela pontualidade e pelo preço justo, com relatos de demora."
  }
}
```

`taxaInconsistencia` e `versaoModelo` ficam no índice mas fora da resposta: são
sinais internos de qualidade, não informação de vitrine.

### Efeito na ordenação

Em `ordenacao=RECOMENDADOS`, o sentimento médio entra como **primeiro critério
de desempate**, logo depois da média de estrelas. Como `qt_avaliacao_geral` é
`DECIMAL(2,1)`, empate é comum — no seed atual quatro profissionais têm 4,6:

| Posição | RECOMENDADOS | AVALIACAO |
|---|---|---|
| 1 | Fabio (0,9318) | Marcos (0,8913) |
| 2 | Marlene (0,9286) | Sandra (0,9155) |
| 3 | Sandra (0,9155) | Fabio (0,9318) |
| 4 | Marcos (0,8913) | Marlene (0,9286) |

`ordenacao=AVALIACAO` e `ordenacao=DISTANCIA` seguem intocadas — só
`RECOMENDADOS` usa o sinal textual.

Quem ainda não tem reputação calculada entra no desempate como **0,5** (o valor
de NEUTRO na escala do PLN), para não ser empurrado para o fim da lista só por
falta de dado.