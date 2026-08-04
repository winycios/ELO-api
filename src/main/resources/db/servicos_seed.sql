USE database_elo;

DROP TEMPORARY TABLE IF EXISTS tmp_seed_servico;
     DROP TEMPORARY TABLE IF EXISTS tmp_seed_imagem;

CREATE TEMPORARY TABLE tmp_seed_servico
(
    seed_key                 VARCHAR(50)  NOT NULL,
    profissional_id          INT          NOT NULL,
    categoria_geral          VARCHAR(100) NOT NULL,
    categoria_especifica     VARCHAR(100) NOT NULL,
    descricao                VARCHAR(200) NOT NULL,
    valor                    DOUBLE       NOT NULL,
    tags                     VARCHAR(200) NOT NULL,
    tipo_execucao            VARCHAR(20)  NOT NULL,
    tempo_experiencia_anos   INT          NOT NULL,
    PRIMARY KEY (seed_key)
);

INSERT INTO tmp_seed_servico
    (seed_key, profissional_id, categoria_geral, categoria_especifica, descricao,
     valor, tags, tipo_execucao, tempo_experiencia_anos)
VALUES
    ('prof1_eletrica', 1, 'Eletricista', 'Instalação elétrica',
     'Instalação elétrica residencial com revisão dos pontos e quadro de energia.',
     180.00, 'eletricista, instalação elétrica, residência', 'presencial', 6),

    ('prof1_chuveiro', 1, 'Eletricista', 'Instalação de chuveiro',
     'Instalação e troca de chuveiro elétrico com teste de funcionamento.',
     100.00, 'chuveiro, instalação, troca, elétrica', 'presencial', 6),

    ('prof1_disjuntor', 1, 'Eletricista', 'Instalação de disjuntores',
     'Instalação e substituição de disjuntores residenciais e comerciais.',
     130.00, 'disjuntor, quadro elétrico, manutenção', 'presencial', 5),

    ('prof3_manutencao_eletrica', 3, 'Eletricista', 'Manutenção elétrica',
     'Diagnóstico e manutenção de falhas elétricas em residências e pequenos comércios.',
     140.00, 'manutenção elétrica, diagnóstico, reparo', 'presencial', 4),

    ('prof3_tomadas', 3, 'Eletricista', 'Instalação de tomadas e interruptores',
     'Instalação de tomadas, interruptores e novos pontos elétricos.',
     110.00, 'tomada, interruptor, ponto elétrico', 'presencial', 4),

    ('prof3_informatica', 3, 'Técnico de Informática', 'Manutenção de computadores',
     'Manutenção preventiva e corretiva de computadores e notebooks.',
     160.00, 'computador, notebook, manutenção, informática', 'presencial', 7);

START TRANSACTION;

INSERT INTO servico
    (fk_id_profissional_usuario, fk_id_categoria_especifica, ds_descricao,
     vl_servico, ds_tag, tp_execucao, st_ativo, nr_tempo_experiencia, dt_atualizacao)
SELECT
    seed.profissional_id,
    categoria_especifica.id_categoria_especifica,
    seed.descricao,
    seed.valor,
    seed.tags,
    seed.tipo_execucao,
    1,
    seed.tempo_experiencia_anos,
    NOW()
FROM tmp_seed_servico seed
JOIN categoria_geral
    ON categoria_geral.nm_categoria = seed.categoria_geral
JOIN categoria_especifica
    ON categoria_especifica.fk_id_categoria_geral = categoria_geral.id_categoria_geral
   AND categoria_especifica.nm_categoria_especifica = seed.categoria_especifica
JOIN profissional
    ON profissional.usuario_id = seed.profissional_id
WHERE NOT EXISTS (
    SELECT 1
    FROM servico existente
    WHERE existente.fk_id_profissional_usuario = seed.profissional_id
      AND existente.fk_id_categoria_especifica = categoria_especifica.id_categoria_especifica
      AND existente.ds_descricao = seed.descricao
);

-- Segunda a sexta, das 08:00 as 18:00.
INSERT INTO servico_disponibilidade
    (fk_id_servico, nr_dia_semana, hr_inicio, hr_fim, st_ativo, dt_criacao)
SELECT
    servico.id_servico,
    dias.dia_semana,
    '08:00:00',
    '18:00:00',
    1,
    NOW()
FROM tmp_seed_servico seed
JOIN categoria_geral
    ON categoria_geral.nm_categoria = seed.categoria_geral
JOIN categoria_especifica
    ON categoria_especifica.fk_id_categoria_geral = categoria_geral.id_categoria_geral
   AND categoria_especifica.nm_categoria_especifica = seed.categoria_especifica
JOIN servico
    ON servico.fk_id_profissional_usuario = seed.profissional_id
   AND servico.fk_id_categoria_especifica = categoria_especifica.id_categoria_especifica
   AND servico.ds_descricao = seed.descricao
CROSS JOIN (
    SELECT 1 AS dia_semana
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
) dias
WHERE NOT EXISTS (
    SELECT 1
    FROM servico_disponibilidade existente
    WHERE existente.fk_id_servico = servico.id_servico
      AND existente.nr_dia_semana = dias.dia_semana
      AND existente.hr_inicio = '08:00:00'
      AND existente.hr_fim = '18:00:00'
);

CREATE TEMPORARY TABLE tmp_seed_imagem
(
    seed_key VARCHAR(50)  NOT NULL,
    url      VARCHAR(500) NOT NULL,
    ordem    INT          NOT NULL,
    PRIMARY KEY (seed_key, ordem)
);

INSERT INTO tmp_seed_imagem (seed_key, url, ordem)
VALUES
    ('prof1_eletrica', 'https://images.unsplash.com/photo-1544724569-5f546fd6f2b5?auto=format&fit=crop&w=1200&q=80', 1),
    ('prof1_eletrica', 'https://images.unsplash.com/photo-1635335874521-7987db781153?auto=format&fit=crop&w=1200&q=80', 2),
    ('prof1_chuveiro', 'https://images.unsplash.com/photo-1695002817411-203c7f19dfa3?auto=format&fit=crop&w=1200&q=80', 1),
    ('prof1_chuveiro', 'https://images.unsplash.com/photo-1620626011761-996317b8d101?auto=format&fit=crop&w=1200&q=80', 2),
    ('prof1_disjuntor', 'https://images.unsplash.com/photo-1758101755915-462eddc23f57?auto=format&fit=crop&w=1200&q=80', 1),
    ('prof1_disjuntor', 'https://images.unsplash.com/photo-1576446470246-499c738d1c8e?auto=format&fit=crop&w=1200&q=80', 2),
    ('prof3_manutencao_eletrica', 'https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?auto=format&fit=crop&w=1200&q=80', 1),
    ('prof3_manutencao_eletrica', 'https://images.unsplash.com/photo-1652715564391-38cc4475b7f5?auto=format&fit=crop&w=1200&q=80', 2),
    ('prof3_tomadas', 'https://images.unsplash.com/photo-1566417110090-6b15a06ec800?auto=format&fit=crop&w=1200&q=80', 1),
    ('prof3_tomadas', 'https://images.unsplash.com/photo-1558054665-fbe00cd7d920?auto=format&fit=crop&w=1200&q=80', 2),
    ('prof3_informatica', 'https://images.unsplash.com/photo-1611396000732-f8c9a933424f?auto=format&fit=crop&w=1200&q=80', 1),
    ('prof3_informatica', 'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?auto=format&fit=crop&w=1200&q=80', 2);

INSERT INTO servico_imagem (fk_id_servico, ds_chave_imagem, nr_ordem)
SELECT
    servico.id_servico,
    imagem.url,
    imagem.ordem
FROM tmp_seed_imagem imagem
JOIN tmp_seed_servico seed
    ON seed.seed_key = imagem.seed_key
JOIN categoria_geral
    ON categoria_geral.nm_categoria = seed.categoria_geral
JOIN categoria_especifica
    ON categoria_especifica.fk_id_categoria_geral = categoria_geral.id_categoria_geral
   AND categoria_especifica.nm_categoria_especifica = seed.categoria_especifica
JOIN servico
    ON servico.fk_id_profissional_usuario = seed.profissional_id
   AND servico.fk_id_categoria_especifica = categoria_especifica.id_categoria_especifica
   AND servico.ds_descricao = seed.descricao
WHERE NOT EXISTS (
    SELECT 1
    FROM servico_imagem existente
    WHERE existente.fk_id_servico = servico.id_servico
      AND existente.nr_ordem = imagem.ordem
);

-- Garante que o worker envie os dois profissionais ao Elasticsearch.
INSERT INTO search_outbox
    (fk_profissional_id, dt_criacao, dt_processamento, nr_tentativas)
SELECT profissional.usuario_id, NOW(3), NULL, 0
FROM profissional
WHERE profissional.usuario_id IN (1, 3)
  AND NOT EXISTS (
      SELECT 1
      FROM search_outbox pendente
      WHERE pendente.fk_profissional_id = profissional.usuario_id
        AND pendente.dt_processamento IS NULL
  );

COMMIT;

DROP TEMPORARY TABLE IF EXISTS tmp_seed_imagem;
DROP TEMPORARY TABLE IF EXISTS tmp_seed_servico;
