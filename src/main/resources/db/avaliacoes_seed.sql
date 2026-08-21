USE database_elo;

SET NAMES utf8mb4;

START TRANSACTION;

-- ---------------------------------------------------------------------
-- 0. Limpeza da faixa reservada (torna o arquivo re-executavel)
-- ---------------------------------------------------------------------
DELETE FROM avaliacao_analise_pln
 WHERE fk_id_avaliacao_reserva IN (
       SELECT id_avaliacao_reserva FROM (
           SELECT id_avaliacao_reserva
             FROM avaliacao_reserva
            WHERE fk_id_reserva BETWEEN 1001 AND 1999
       ) alvo
 );

DELETE FROM profissional_reputacao_pln WHERE fk_id_profissional BETWEEN 101 AND 114;

DELETE FROM avaliacao_reserva WHERE fk_id_reserva BETWEEN 1001 AND 1999;

DELETE FROM notificacao_outbox
 WHERE fk_notificacao_id IN (
       SELECT id_notificacao FROM (
           SELECT id_notificacao FROM notificacao WHERE fk_orcamento_id BETWEEN 1001 AND 1999
       ) alvo
 );
DELETE FROM notificacao WHERE fk_orcamento_id BETWEEN 1001 AND 1999;

DELETE FROM orcamento_custos  WHERE fk_id_orcamento BETWEEN 1001 AND 1999;
DELETE FROM orcamento_endereco WHERE fk_id_orcamento BETWEEN 1001 AND 1999;
DELETE FROM orcamento_imagem  WHERE fk_id_orcamento BETWEEN 1001 AND 1999;
DELETE FROM orcamento         WHERE id_orcamento   BETWEEN 1001 AND 1999;

DELETE FROM servico_disponibilidade
 WHERE fk_id_servico IN (
       SELECT id_servico FROM (
           SELECT id_servico FROM servico WHERE fk_id_profissional_usuario BETWEEN 101 AND 114
       ) alvo
 );
DELETE FROM servico_imagem
 WHERE fk_id_servico IN (
       SELECT id_servico FROM (
           SELECT id_servico FROM servico WHERE fk_id_profissional_usuario BETWEEN 101 AND 114
       ) alvo
 );
DELETE FROM servico          WHERE fk_id_profissional_usuario BETWEEN 101 AND 114;
DELETE FROM search_outbox    WHERE fk_profissional_id BETWEEN 101 AND 114;
DELETE FROM area_atendimento WHERE fk_id_profissional  BETWEEN 101 AND 114;
DELETE FROM endereco_usuario WHERE fk_usuario_id BETWEEN 201 AND 236;
DELETE FROM profissional     WHERE usuario_id BETWEEN 101 AND 114;
DELETE FROM usuario          WHERE id_usuario BETWEEN 101 AND 114
                                OR id_usuario BETWEEN 201 AND 236;

-- ---------------------------------------------------------------------
-- 1. Usuarios
--    O hash de senha e o mesmo do users_seed.sql, entao todos entram com
--    a mesma senha do usuario profissional@gmail.com.
-- ---------------------------------------------------------------------
SET @senha := '$2a$10$ZKgvowvdRsZHd71Wc7YVgebPXSDcW3PfZT/YNYr4V1ihjkqea4g/q';

INSERT INTO usuario (id_usuario, nm_nome, nm_sobrenome, ds_email, senha, dt_criacao, tel_celular, tel_whats, st_habilitado)
VALUES
    (101, 'Marcos',    'Vieira',   'marcos.vieira@elo.test',    @senha, '2026-01-08 09:12:00', '11987650101', '11987650101', 1),
    (102, 'Rafael',    'Nogueira', 'rafael.nogueira@elo.test',  @senha, '2026-01-09 14:30:00', '11987650102', '11987650102', 1),
    (103, 'Sandra',    'Alencar',  'sandra.alencar@elo.test',   @senha, '2026-01-10 08:05:00', '11987650103', '11987650103', 1),
    (104, 'Vera',      'Prado',    'vera.prado@elo.test',       @senha, '2026-01-11 11:45:00', '11987650104', '11987650104', 1),
    (105, 'Joel',      'Bastos',   'joel.bastos@elo.test',      @senha, '2026-01-12 16:20:00', '11987650105', '11987650105', 1),
    (106, 'Cleber',    'Matos',    'cleber.matos@elo.test',     @senha, '2026-01-13 10:00:00', '11987650106', '11987650106', 1),
    (107, 'Adriana',   'Peixoto',  'adriana.peixoto@elo.test',  @senha, '2026-01-14 13:15:00', '11987650107', '11987650107', 1),
    (108, 'Wellington','Souza',    'wellington.souza@elo.test', @senha, '2026-01-15 09:40:00', '11987650108', '11987650108', 1),
    (109, 'Iranildo',  'Ramos',    'iranildo.ramos@elo.test',   @senha, '2026-01-16 15:55:00', '11987650109', '11987650109', 1),
    (110, 'Fabio',     'Kenji',    'fabio.kenji@elo.test',      @senha, '2026-01-17 08:30:00', '11987650110', '11987650110', 1),
    (111, 'Nilton',    'Barreto',  'nilton.barreto@elo.test',   @senha, '2026-01-18 12:10:00', '11987650111', '11987650111', 1),
    (112, 'Gerson',    'Pinheiro', 'gerson.pinheiro@elo.test',  @senha, '2026-01-19 17:25:00', '11987650112', '11987650112', 1),
    (113, 'Marlene',   'Duarte',   'marlene.duarte@elo.test',   @senha, '2026-01-20 07:50:00', '11987650113', '11987650113', 1),
    (114, 'Sidney',    'Camargo',  'sidney.camargo@elo.test',   @senha, '2026-01-21 14:05:00', '11987650114', '11987650114', 1);

-- Clientes 201-236.
INSERT INTO usuario (id_usuario, nm_nome, nm_sobrenome, ds_email, senha, dt_criacao, tel_celular, tel_whats, st_habilitado)
VALUES
    (201, 'Beatriz',  'Almeida',   'beatriz.almeida@elo.test',  @senha, '2026-01-22 09:00:00', '11976540201', '11976540201', 1),
    (202, 'Carlos',   'Menezes',   'carlos.menezes@elo.test',   @senha, '2026-01-22 10:00:00', '11976540202', '11976540202', 1),
    (203, 'Daniela',  'Rocha',     'daniela.rocha@elo.test',    @senha, '2026-01-22 11:00:00', '11976540203', '11976540203', 1),
    (204, 'Eduardo',  'Lima',      'eduardo.lima@elo.test',     @senha, '2026-01-22 12:00:00', '11976540204', '11976540204', 1),
    (205, 'Fernanda', 'Castro',    'fernanda.castro@elo.test',  @senha, '2026-01-22 13:00:00', '11976540205', '11976540205', 1),
    (206, 'Gustavo',  'Pereira',   'gustavo.pereira@elo.test',  @senha, '2026-01-22 14:00:00', '11976540206', '11976540206', 1),
    (207, 'Helena',   'Barbosa',   'helena.barbosa@elo.test',   @senha, '2026-01-22 15:00:00', '11976540207', '11976540207', 1),
    (208, 'Igor',     'Fontes',    'igor.fontes@elo.test',      @senha, '2026-01-22 16:00:00', '11976540208', '11976540208', 1),
    (209, 'Juliana',  'Moreira',   'juliana.moreira@elo.test',  @senha, '2026-01-23 09:00:00', '11976540209', '11976540209', 1),
    (210, 'Kleber',   'Antunes',   'kleber.antunes@elo.test',   @senha, '2026-01-23 10:00:00', '11976540210', '11976540210', 1),
    (211, 'Larissa',  'Freitas',   'larissa.freitas@elo.test',  @senha, '2026-01-23 11:00:00', '11976540211', '11976540211', 1),
    (212, 'Marcelo',  'Tavares',   'marcelo.tavares@elo.test',  @senha, '2026-01-23 12:00:00', '11976540212', '11976540212', 1),
    (213, 'Natalia',  'Cardoso',   'natalia.cardoso@elo.test',  @senha, '2026-01-23 13:00:00', '11976540213', '11976540213', 1),
    (214, 'Otavio',   'Marinho',   'otavio.marinho@elo.test',   @senha, '2026-01-23 14:00:00', '11976540214', '11976540214', 1),
    (215, 'Patricia', 'Gomes',     'patricia.gomes@elo.test',   @senha, '2026-01-23 15:00:00', '11976540215', '11976540215', 1),
    (216, 'Quirino',  'Serra',     'quirino.serra@elo.test',    @senha, '2026-01-23 16:00:00', '11976540216', '11976540216', 1),
    (217, 'Renata',   'Xavier',    'renata.xavier@elo.test',    @senha, '2026-01-24 09:00:00', '11976540217', '11976540217', 1),
    (218, 'Samuel',   'Aguiar',    'samuel.aguiar@elo.test',    @senha, '2026-01-24 10:00:00', '11976540218', '11976540218', 1),
    (219, 'Tatiana',  'Borges',    'tatiana.borges@elo.test',   @senha, '2026-01-24 11:00:00', '11976540219', '11976540219', 1),
    (220, 'Ubirajara','Neves',     'ubirajara.neves@elo.test',  @senha, '2026-01-24 12:00:00', '11976540220', '11976540220', 1),
    (221, 'Vanessa',  'Quintela',  'vanessa.quintela@elo.test', @senha, '2026-01-24 13:00:00', '11976540221', '11976540221', 1),
    (222, 'Wagner',   'Teles',     'wagner.teles@elo.test',     @senha, '2026-01-24 14:00:00', '11976540222', '11976540222', 1),
    (223, 'Ximena',   'Duarte',    'ximena.duarte@elo.test',    @senha, '2026-01-24 15:00:00', '11976540223', '11976540223', 1),
    (224, 'Yuri',     'Salgado',   'yuri.salgado@elo.test',     @senha, '2026-01-24 16:00:00', '11976540224', '11976540224', 1),
    (225, 'Zilda',    'Ferraz',    'zilda.ferraz@elo.test',     @senha, '2026-01-25 09:00:00', '11976540225', '11976540225', 1),
    (226, 'Alberto',  'Siqueira',  'alberto.siqueira@elo.test', @senha, '2026-01-25 10:00:00', '11976540226', '11976540226', 1),
    (227, 'Bruna',    'Coelho',    'bruna.coelho@elo.test',     @senha, '2026-01-25 11:00:00', '11976540227', '11976540227', 1),
    (228, 'Cesar',    'Maia',      'cesar.maia@elo.test',       @senha, '2026-01-25 12:00:00', '11976540228', '11976540228', 1),
    (229, 'Denise',   'Vasques',   'denise.vasques@elo.test',   @senha, '2026-01-25 13:00:00', '11976540229', '11976540229', 1),
    (230, 'Elias',    'Prudente',  'elias.prudente@elo.test',   @senha, '2026-01-25 14:00:00', '11976540230', '11976540230', 1),
    (231, 'Flavia',   'Rangel',    'flavia.rangel@elo.test',    @senha, '2026-01-25 15:00:00', '11976540231', '11976540231', 1),
    (232, 'Geraldo',  'Sampaio',   'geraldo.sampaio@elo.test',  @senha, '2026-01-25 16:00:00', '11976540232', '11976540232', 1),
    (233, 'Heloisa',  'Nunes',     'heloisa.nunes@elo.test',    @senha, '2026-01-26 09:00:00', '11976540233', '11976540233', 1),
    (234, 'Ivan',     'Palmeira',  'ivan.palmeira@elo.test',    @senha, '2026-01-26 10:00:00', '11976540234', '11976540234', 1),
    (235, 'Jussara',  'Bentes',    'jussara.bentes@elo.test',   @senha, '2026-01-26 11:00:00', '11976540235', '11976540235', 1),
    (236, 'Lucas',    'Andrade',   'lucas.andrade@elo.test',    @senha, '2026-01-26 12:00:00', '11976540236', '11976540236', 1);

-- ---------------------------------------------------------------------
-- 2. Perfis profissionais e area de atendimento
-- ---------------------------------------------------------------------
INSERT INTO profissional (usuario_id, dt_criacao, st_disponivel, ds_apresentacao, ds_especialidades, st_habilitado, qt_servicos_concluido)
VALUES
    (101, '2026-01-08 09:12:00', 1, 'Eletricista com 12 anos de experiencia em residencias e pequenos comercios.', 'Instalacao eletrica, quadro de energia, chuveiro', 1, 0),
    (102, '2026-01-09 14:30:00', 1, 'Servicos eletricos em geral, atendimento na regiao central.',               'Manutencao eletrica, tomadas, disjuntores',      1, 0),
    (103, '2026-01-10 08:05:00', 1, 'Diarista com referencias, limpeza residencial e organizacao.',              'Limpeza residencial, organizacao, passadoria',   1, 0),
    (104, '2026-01-11 11:45:00', 1, 'Faxina e limpeza pesada, atendo toda a zona leste.',                        'Limpeza pesada, pos-obra',                       1, 0),
    (105, '2026-01-12 16:20:00', 1, 'Encanador 24 horas, deteccao de vazamento sem quebra-quebra.',              'Vazamento, desentupimento, hidraulica',          1, 0),
    (106, '2026-01-13 10:00:00', 1, 'Pintor residencial, textura e massa corrida com acabamento fino.',          'Pintura residencial, textura, massa corrida',    1, 0),
    (107, '2026-01-14 13:15:00', 1, 'Jardinagem e paisagismo, cuido do seu jardim o ano todo.',                  'Poda, corte de grama, paisagismo',               1, 0),
    (108, '2026-01-15 09:40:00', 1, 'Montagem e desmontagem de moveis, atendimento no mesmo dia.',               'Guarda-roupa, planejados, prateleiras',          1, 0),
    (109, '2026-01-16 15:55:00', 1, 'Pedreiro para pequenas reformas e acabamentos.',                            'Parede, reboco, contrapiso',                     1, 0),
    (110, '2026-01-17 08:30:00', 1, 'Tecnico de informatica, atendimento presencial e remoto.',                  'Formatacao, remocao de virus, redes',            1, 0),
    (111, '2026-01-18 12:10:00', 1, 'Instalacao e manutencao de ar-condicionado split e janela.',                'Instalacao, limpeza, recarga de gas',            1, 0),
    (112, '2026-01-19 17:25:00', 1, 'Chaveiro 24 horas, abertura sem danos e fechadura digital.',                'Abertura de portas, fechaduras, copias',         1, 0),
    (113, '2026-01-20 07:50:00', 1, 'Cuidadora de idosos com curso tecnico e 8 anos de experiencia.',            'Acompanhamento domiciliar e hospitalar',         1, 0),
    (114, '2026-01-21 14:05:00', 1, 'Fretes e mudancas, caminhao bau proprio e ajudantes.',                      'Mudanca residencial, pequenos fretes',           1, 0);

INSERT INTO area_atendimento (fk_id_profissional, nr_latitude, nr_longitude, nr_raio, nm_cidade, nm_estado, nm_bairro, dt_criacao)
VALUES
    (101, -23.5505, -46.6333, 20, 'Sao Paulo', 'SP', 'Se',              '2026-01-08 09:12:00'),
    (102, -23.5629, -46.6544, 15, 'Sao Paulo', 'SP', 'Bela Vista',      '2026-01-09 14:30:00'),
    (103, -23.5980, -46.6800, 18, 'Sao Paulo', 'SP', 'Pinheiros',       '2026-01-10 08:05:00'),
    (104, -23.5410, -46.5200, 25, 'Sao Paulo', 'SP', 'Itaquera',        '2026-01-11 11:45:00'),
    (105, -23.5330, -46.6390, 30, 'Sao Paulo', 'SP', 'Santana',         '2026-01-12 16:20:00'),
    (106, -23.6100, -46.6600, 22, 'Sao Paulo', 'SP', 'Vila Mariana',    '2026-01-13 10:00:00'),
    (107, -23.6280, -46.7100, 20, 'Sao Paulo', 'SP', 'Santo Amaro',     '2026-01-14 13:15:00'),
    (108, -23.5750, -46.6420, 25, 'Sao Paulo', 'SP', 'Paraiso',         '2026-01-15 09:40:00'),
    (109, -23.5200, -46.5600, 20, 'Sao Paulo', 'SP', 'Penha',           '2026-01-16 15:55:00'),
    (110, -23.5870, -46.6820, 35, 'Sao Paulo', 'SP', 'Butanta',         '2026-01-17 08:30:00'),
    (111, -23.5660, -46.7200, 28, 'Sao Paulo', 'SP', 'Lapa',            '2026-01-18 12:10:00'),
    (112, -23.5480, -46.6400, 30, 'Sao Paulo', 'SP', 'Republica',       '2026-01-19 17:25:00'),
    (113, -23.6010, -46.6650, 18, 'Sao Paulo', 'SP', 'Saude',           '2026-01-20 07:50:00'),
    (114, -23.5300, -46.6900, 40, 'Sao Paulo', 'SP', 'Casa Verde',      '2026-01-21 14:05:00');

-- ---------------------------------------------------------------------
-- 3. Servicos
--    Mesma abordagem do servicos_seed.sql: tabela temporaria com o nome
--    da categoria, resolvido por join, para nao depender de id fixo.
-- ---------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_pln_servico_seed;
CREATE TEMPORARY TABLE tmp_pln_servico_seed
(
    profissional_id      INT          NOT NULL,
    categoria_geral      VARCHAR(100) NOT NULL,
    categoria_especifica VARCHAR(100) NOT NULL,
    descricao            VARCHAR(200) NOT NULL,
    valor                DOUBLE       NOT NULL,
    tags                 VARCHAR(200) NOT NULL,
    tipo_execucao        VARCHAR(20)  NOT NULL,
    experiencia          INT          NOT NULL
);

-- Os nomes de categoria abaixo precisam bater com categorias_seed.sql, por
-- isso vao acentuados. O resto do arquivo e ASCII de proposito: o texto das
-- avaliacoes passa por remover_acentos() no PLN antes de virar caracteristica,
-- entao acento ali nao muda nada e so aumentaria o risco de mojibake ao
-- importar o arquivo.
INSERT INTO tmp_pln_servico_seed VALUES
    (101, 'Eletricista', 'Instalação elétrica',                     'Instalacao eletrica residencial completa com revisao do quadro.',        220.00, 'eletricista, instalacao, residencial', 'presencial', 12),
    (101, 'Eletricista', 'Instalação de chuveiro',                  'Troca e instalacao de chuveiro eletrico com teste de aterramento.',      120.00, 'chuveiro, troca, eletrica',            'presencial', 12),
    (101, 'Eletricista', 'Instalação de disjuntores',               'Organizacao de quadro e substituicao de disjuntores.',                  160.00, 'disjuntor, quadro, seguranca',         'presencial', 12),
    (102, 'Eletricista', 'Manutenção elétrica',                     'Diagnostico e reparo de falhas eletricas em geral.',                    130.00, 'manutencao, reparo, diagnostico',      'presencial',  4),
    (102, 'Eletricista', 'Instalação de tomadas e interruptores',   'Novos pontos de tomada e troca de interruptores.',                       90.00, 'tomada, interruptor, ponto',           'presencial',  4),
    (103, 'Diarista',    'Limpeza residencial',                     'Faxina completa de apartamento ou casa, produtos inclusos.',            180.00, 'faxina, limpeza, residencial',         'presencial',  9),
    (103, 'Diarista',    'Organização de ambientes',                'Organizacao de armarios, closet e despensa.',                           200.00, 'organizacao, closet, armario',         'presencial',  9),
    (103, 'Diarista',    'Passadoria de roupas',                    'Passadoria de roupas por periodo.',                                     140.00, 'passar roupa, passadoria',             'presencial',  9),
    (104, 'Diarista',    'Limpeza pesada',                          'Limpeza pesada de imoveis desocupados.',                                190.00, 'limpeza pesada, desocupado',           'presencial',  3),
    (104, 'Diarista',    'Limpeza pós-obra',                        'Remocao de residuo fino de obra e polimento.',                          260.00, 'pos-obra, residuo, polimento',         'presencial',  3),
    (105, 'Encanador',   'Reparo de vazamentos',                    'Deteccao e reparo de vazamento sem quebrar parede.',                    280.00, 'vazamento, deteccao, hidraulica',      'presencial', 15),
    (105, 'Encanador',   'Desentupimento',                          'Desentupimento de pia, ralo e vaso sanitario.',                         200.00, 'desentupimento, ralo, pia',            'presencial', 15),
    (105, 'Encanador',   'Manutenção hidráulica',                   'Manutencao preventiva da rede hidraulica do imovel.',                   240.00, 'hidraulica, manutencao, preventiva',   'presencial', 15),
    (106, 'Pintor',      'Pintura residencial',                     'Pintura interna de comodos com massa e duas demaos.',                   950.00, 'pintura, interna, residencial',        'presencial',  8),
    (106, 'Pintor',      'Aplicação de textura',                    'Aplicacao de textura decorativa em paredes internas.',                  680.00, 'textura, decorativa, parede',          'presencial',  8),
    (106, 'Pintor',      'Aplicação de massa corrida',              'Preparo de parede com massa corrida e lixamento.',                      520.00, 'massa corrida, preparo, lixamento',    'presencial',  8),
    (107, 'Jardineiro',  'Manutenção de jardim',                    'Manutencao mensal de jardim com poda e adubacao.',                      320.00, 'jardim, poda, adubacao',               'presencial',  6),
    (107, 'Jardineiro',  'Corte de grama',                          'Corte de grama com recolhimento de residuo.',                           150.00, 'grama, corte, quintal',                'presencial',  6),
    (107, 'Jardineiro',  'Paisagismo',                              'Projeto e execucao de paisagismo residencial.',                         890.00, 'paisagismo, projeto, plantas',         'presencial',  6),
    (108, 'Montador de Móveis', 'Montagem de guarda-roupa',         'Montagem de guarda-roupa de ate seis portas.',                          180.00, 'montagem, guarda-roupa, moveis',       'presencial',  7),
    (108, 'Montador de Móveis', 'Instalação de prateleiras',        'Instalacao de prateleiras e nichos com nivelamento.',                   110.00, 'prateleira, nicho, instalacao',        'presencial',  7),
    (108, 'Montador de Móveis', 'Montagem de móveis de escritório', 'Montagem de estacao de trabalho e cadeiras.',                           160.00, 'escritorio, estacao, montagem',        'presencial',  7),
    (109, 'Pedreiro',    'Pequenas reformas',                       'Pequenas reformas de alvenaria e acabamento.',                          750.00, 'reforma, alvenaria, acabamento',       'presencial',  5),
    (109, 'Pedreiro',    'Reboco',                                  'Reboco de paredes internas e externas.',                                600.00, 'reboco, parede, massa',                'presencial',  5),
    (110, 'Técnico de Informática', 'Formatação de computadores',   'Formatacao com backup dos arquivos e instalacao de drivers.',           150.00, 'formatacao, backup, notebook',         'presencial', 10),
    (110, 'Técnico de Informática', 'Remoção de vírus',             'Remocao de virus e configuracao de antivirus.',                         130.00, 'virus, antivirus, seguranca',          'remoto',     10),
    (110, 'Técnico de Informática', 'Configuração de redes',        'Configuracao de rede wifi, roteador e cabeamento.',                     180.00, 'rede, wifi, roteador',                 'presencial', 10),
    (111, 'Técnico de Ar-Condicionado', 'Instalação de ar-condicionado', 'Instalacao de split ate 12 mil BTUs com tubulacao.',               550.00, 'split, instalacao, ar condicionado',   'presencial',  9),
    (111, 'Técnico de Ar-Condicionado', 'Limpeza de ar-condicionado',    'Higienizacao completa de split com produto bactericida.',          180.00, 'limpeza, higienizacao, split',         'presencial',  9),
    (111, 'Técnico de Ar-Condicionado', 'Recarga de gás',                'Recarga de gas refrigerante com teste de vazamento.',              320.00, 'gas, recarga, refrigeracao',           'presencial',  9),
    (112, 'Chaveiro',    'Abertura de portas',                      'Abertura de porta sem danos, atendimento 24 horas.',                    180.00, 'abertura, porta, urgencia',            'presencial', 11),
    (112, 'Chaveiro',    'Troca de fechaduras',                     'Troca de fechadura comum e tetra-chave.',                               220.00, 'fechadura, troca, seguranca',          'presencial', 11),
    (112, 'Chaveiro',    'Instalação de fechaduras digitais',       'Instalacao e configuracao de fechadura digital.',                       420.00, 'fechadura digital, biometria',         'presencial', 11),
    (113, 'Cuidador de Idosos', 'Acompanhamento domiciliar',        'Acompanhamento domiciliar diurno com controle de medicacao.',           260.00, 'cuidadora, idoso, domiciliar',         'presencial',  8),
    (113, 'Cuidador de Idosos', 'Acompanhamento hospitalar',        'Acompanhamento hospitalar em periodo integral.',                        320.00, 'hospital, acompanhante, idoso',        'presencial',  8),
    (114, 'Frete e Mudanças', 'Mudança residencial',                'Mudanca residencial com caminhao bau e dois ajudantes.',               1200.00, 'mudanca, caminhao, ajudante',          'presencial',  6),
    (114, 'Frete e Mudanças', 'Pequenos fretes',                    'Pequenos fretes e transporte de moveis avulsos.',                       300.00, 'frete, transporte, moveis',            'presencial',  6);

INSERT INTO servico
    (fk_id_profissional_usuario, fk_id_categoria_especifica, ds_descricao, vl_servico,
     ds_tag, tp_execucao, st_ativo, nr_tempo_experiencia, dt_atualizacao, qt_reservado, nr_avaliacao_geral)
SELECT seed.profissional_id,
       ce.id_categoria_especifica,
       seed.descricao,
       seed.valor,
       seed.tags,
       seed.tipo_execucao,
       1,
       seed.experiencia,
       NOW(),
       0,
       NULL
  FROM tmp_pln_servico_seed seed
  JOIN categoria_geral cg
    ON cg.nm_categoria COLLATE utf8mb3_general_ci = seed.categoria_geral COLLATE utf8mb3_general_ci
  JOIN categoria_especifica ce
    ON ce.fk_id_categoria_geral = cg.id_categoria_geral
   AND ce.nm_categoria_especifica COLLATE utf8mb3_general_ci = seed.categoria_especifica COLLATE utf8mb3_general_ci;

DROP TEMPORARY TABLE IF EXISTS tmp_pln_servico_seed;

-- ---------------------------------------------------------------------
-- 4. Comentarios
--
--    Uma linha por avaliacao. `seq` vira o id do orcamento (1000 + seq) e
--    define, por resto de divisao, o cliente e qual servico do profissional
--    foi contratado -- entao nao ha id de servico escrito a mao aqui.
-- ---------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_pln_avaliacao;
CREATE TEMPORARY TABLE tmp_pln_avaliacao
(
    seq             INT          NOT NULL,
    profissional_id INT          NOT NULL,
    nota            TINYINT      NOT NULL,
    comentario      VARCHAR(200) NULL,
    dt_servico      DATETIME     NULL,
    PRIMARY KEY (seq)
);

-- --- 101 Marcos Vieira (eletricista) -- reputacao alta -----------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (  1, 101, 5, 'Chegou no horario combinado e deixou tudo funcionando. Trabalho limpo, levou o entulho embora.'),
 (  2, 101, 5, 'Resolveu um curto que dois eletricistas antes nao acharam. Muito experiente.'),
 (  3, 101, 5, 'Pontual, educado e cobrou exatamente o que tinha passado no orcamento.'),
 (  4, 101, 4, 'Servico bem feito. So demorou um pouco para conseguir data na agenda.'),
 (  5, 101, 5, 'Nao atrasou nem um minuto e ainda explicou tudo o que estava fazendo.'),
 (  6, 101, 5, 'Instalou o chuveiro e testou na frente da gente. Sem duvida chamo de novo.'),
 (  7, 101, 4, 'Bom profissional, acabamento caprichado. O preco ficou um pouco acima do que eu esperava.'),
 (  8, 101, 5, 'Nada a reclamar. Trouxe todas as ferramentas e o material necessario.'),
 (  9, 101, 5, 'Excelente! Trocou a fiacao do apartamento inteiro em dois dias.'),
 ( 10, 101, 3, 'O servico ficou bom, mas remarcou duas vezes antes de vir.'),
 ( 11, 101, 5, 'Muito atencioso, tirou todas as minhas duvidas por telefone antes de fechar.'),
 ( 12, 101, 4, 'Chegou vinte minutos atrasado, porem avisou e compensou terminando antes.'),
 ( 13, 101, 5, 'Profissional de confianca, deixei ele sozinho em casa sem preocupacao nenhuma.'),
 ( 14, 101, 5, 'otimo trabalho, recomendo dms! vlw marcos'),
 ( 15, 101, 5, 'Organizou o quadro de disjuntores que estava um horror. Ficou impecavel.'),
 ( 16, 101, 4, 'Fez o combinado direitinho. So achei que poderia ter limpado melhor depois.'),
 ( 17, 101, 2, 'Cobrou uma taxa de deslocamento que nao tinha sido combinada. O servico em si ficou bom.'),
 ( 18, 101, 5, 'Salvou meu domingo, veio de urgencia quando a energia caiu.'),
 ( 19, 101, 5, 'Trabalho impecavel e preco justo. Ja indiquei para dois vizinhos.'),
 ( 20, 101, 4, 'Resolveu o problema. A comunicacao poderia ser melhor, demorou a responder no aplicativo.'),
 ( 21, 101, 5, 'PONTUAL, CAPRICHOSO E HONESTO. RECOMENDO DE OLHOS FECHADOS'),
 ( 22, 101, 5, 'Nao deixou sujeira nenhuma e ainda ajustou uma tomada extra sem cobrar a mais.');

-- --- 102 Rafael Nogueira (eletricista) -- reputacao mediana ------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 ( 23, 102, 3, 'Fez o servico, mas deixou os fios aparentes. Funciona, so nao ficou bonito.'),
 ( 24, 102, 2, 'Atrasou quase duas horas e nao avisou. O trabalho em si foi razoavel.'),
 ( 25, 102, 4, 'Resolveu rapido e o preco foi camarada.'),
 ( 26, 102, 3, 'Nem sempre pontual, mas na media cumpre o que promete.'),
 ( 27, 102, 1, 'Marcou tres vezes e nao apareceu em nenhuma. Perdi o dia esperando.'),
 ( 28, 102, 3, 'O acabamento poderia ser melhor, mas resolveu o problema.'),
 ( 29, 102, 4, 'Bom atendimento, explicou direitinho o que precisava ser trocado.'),
 ( 30, 102, 2, 'Cobrou mais caro do que o orcamento inicial sem me avisar antes.'),
 ( 31, 102, 3, 'Servico dentro do esperado, nada excepcional.'),
 ( 32, 102, 5, 'Dessa vez veio no horario e caprichou. Melhorou bastante.'),
 ( 33, 102, 1, 'Nao resolveu o problema e ainda queimou uma tomada. Nao recomendo.'),
 ( 34, 102, 3, 'Deu retorno quando cobrei, nao antes.'),
 ( 35, 102, 4, 'Trabalho ok, so precisei pedir para limpar depois.'),
 ( 36, 102, 2, 'Deixou o servico pela metade e disse que voltava. Nunca mais apareceu.'),
 ( 37, 102, 3, 'Preco justo para o que foi feito, mas a comunicacao e confusa.'),
 ( 38, 102, 4, 'Chegou fora do horario, porem terminou dentro do prazo.'),
 ( 39, 102, 5, 'Muito bom! Instalou tudo certinho e testou ponto por ponto.'),
 ( 40, 102, 2, 'Educado, mas nao trouxe as ferramentas necessarias e teve que sair no meio.'),
 ( 41, 102, 3, 'Resolveu, embora tenha demorado mais do que o combinado.'),
 ( 42, 102, 1, 'Pessima experiencia. Estourou o prazo e ainda foi grosseiro quando cobrei.'),
 ( 43, 102, 4, 'Nada demais, mas fez o que tinha que fazer sem enrolacao.'),
 ( 44, 102, 3, 'Recomendaria se o prazo nao fosse tao apertado.');

-- --- 103 Sandra Alencar (diarista) -- reputacao alta -------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 ( 45, 103, 5, 'Casa impecavel, ela e caprichosa demais. Organizou ate os armarios.'),
 ( 46, 103, 5, 'Pontualissima e muito discreta. Confio totalmente nela.'),
 ( 47, 103, 5, 'Deixou tudo brilhando. Passou as roupas melhor do que a lavanderia.'),
 ( 48, 103, 4, 'Otimo trabalho, so nao deu tempo de terminar a area de servico.'),
 ( 49, 103, 5, 'Limpeza pos-obra bem feita, tirou toda a poeira de cimento.'),
 ( 50, 103, 5, 'Nunca atrasou nas cinco vezes que contratei.'),
 ( 51, 103, 5, 'Educada, respeitosa e muito rapida. Vale cada centavo.'),
 ( 52, 103, 4, 'Boa profissional. O valor e um pouco acima da media da regiao.'),
 ( 53, 103, 5, 'Sem duvida a melhor diarista que ja contratei.'),
 ( 54, 103, 5, 'Chegou adiantada e ainda terminou antes do combinado.'),
 ( 55, 103, 3, 'Limpeza boa, mas esqueceu de tirar o lixo como eu tinha pedido.'),
 ( 56, 103, 5, 'Trouxe os proprios produtos e nao cobrou a mais por isso.'),
 ( 57, 103, 5, 'sandra e otima!! casa ficou cheirosa e organizada, super indico'),
 ( 58, 103, 4, 'Trabalho caprichado. So combinar bem os horarios porque a agenda dela e cheia.'),
 ( 59, 103, 5, 'Cuidou das minhas plantas e dos gatos sem eu nem pedir. Atenciosa demais.'),
 ( 60, 103, 5, 'Nada a reclamar, do inicio ao fim.'),
 ( 61, 103, 5, 'Organizou a cozinha inteira em tres horas. Muito eficiente.'),
 ( 62, 103, 4, 'Boa, mas precisei explicar duas vezes como eu queria a rouparia.'),
 ( 63, 103, 5, 'Pessoa de confianca, deixo as chaves com ela tranquila.'),
 ( 64, 103, 5, 'Limpeza pesada muito bem feita, tirou mofo do box que eu ja tinha desistido.'),
 ( 65, 103, 2, 'Faltou no dia marcado. Avisou em cima da hora, mas o trabalho dela e bom.'),
 ( 66, 103, 5, 'Chegou no horario, trabalhou sem parar e ainda sobrou tempo.');

-- --- 104 Vera Prado (diarista) -- reputacao baixa ----------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 ( 67, 104, 1, 'Chegou duas horas atrasada e saiu antes do combinado. Nao recomendo.'),
 ( 68, 104, 2, 'A casa nao ficou limpa como eu esperava. Passou pano so por cima.'),
 ( 69, 104, 1, 'Quebrou um vaso e nao avisou. Descobri depois que ela ja tinha saido.'),
 ( 70, 104, 3, 'Faz o basico. Se voce nao for exigente, resolve.'),
 ( 71, 104, 2, 'Mexeu no celular boa parte do tempo. O servico rendeu pouco.'),
 ( 72, 104, 1, 'Nao fez o que foi combinado. Pedi limpeza pesada e ela fez uma faxina simples.'),
 ( 73, 104, 2, 'Educada, mas muito lenta. Nao terminou nem metade em seis horas.'),
 ( 74, 104, 3, 'Deu para o gasto, embora eu tenha tido que refazer o banheiro depois.'),
 ( 75, 104, 1, 'Pessimo. Deixou o entulho da propria limpeza dentro de casa.'),
 ( 76, 104, 2, 'Cobrou valor de diaria cheia e trabalhou meio periodo.'),
 ( 77, 104, 1, 'Nunca mais contrato. Sumiu no meio do servico e nao atendeu o telefone.'),
 ( 78, 104, 4, 'Dessa vez foi melhor. Parece que entendeu o que eu queria.'),
 ( 79, 104, 2, 'Nao avisou que ia atrasar. Fiquei esperando na porta.'),
 ( 80, 104, 3, 'Limpeza mediana, nada demais.'),
 ( 81, 104, 1, 'Desorganizada, deixou os produtos espalhados e o balde no meio da sala.'),
 ( 82, 104, 2, 'Falta capricho no acabamento. Cantos e rodapes ficaram sujos.'),
 ( 83, 104, 5, 'Comigo foi otima, chegou na hora e caprichou. Nao sei das outras avaliacoes.'),
 ( 84, 104, 1, 'Sem compromisso nenhum. Remarcou quatro vezes seguidas.'),
 ( 85, 104, 2, 'O preco e baixo, mas o resultado tambem.'),
 ( 86, 104, 3, 'Faz o servico, so precisa ser cobrada o tempo todo.'),
 ( 87, 104, 1, 'NAO RECOMENDO. TRABALHO MAL FEITO E AINDA RECLAMOU DO MEU CACHORRO.'),
 ( 88, 104, 2, 'Nao trouxe produto nenhum, tive que comprar tudo na hora.');

-- --- 105 Joel Bastos (encanador) -- bom, mas caro ----------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 ( 89, 105, 5, 'Achou o vazamento em vinte minutos. Muita experiencia, resolveu sem titubear.'),
 ( 90, 105, 4, 'Servico excelente, mas o preco e salgado.'),
 ( 91, 105, 5, 'Desentupiu a pia e ainda explicou como evitar que entupa de novo.'),
 ( 92, 105, 3, 'Resolveu, porem cobrou caro demais para o tempo que ficou aqui.'),
 ( 93, 105, 5, 'Nao quebrou nem um azulejo para achar o cano. Profissional de primeira.'),
 ( 94, 105, 4, 'Pontual e organizado. So o orcamento que veio acima do que eu imaginava.'),
 ( 95, 105, 5, 'Veio de madrugada numa emergencia. Salvou minha casa de inundar.'),
 ( 96, 105, 2, 'Bom tecnico, mas cobrou o dobro do que outro tinha orcado.'),
 ( 97, 105, 5, 'Trabalho limpo, levou todo o entulho e deixou o banheiro seco.'),
 ( 98, 105, 4, 'Cumpriu o prazo prometido sem precisar cobrar.'),
 ( 99, 105, 5, 'Deu garantia de seis meses por escrito. Isso me deixou seguro.'),
 (100, 105, 3, 'O conserto durou pouco, precisei chamar de novo em dois meses.'),
 (101, 105, 5, 'Educado, discreto e respeitoso dentro de casa.'),
 (102, 105, 4, 'Resolveu o problema da caixa de agua. Preco justo pelo trabalho.'),
 (103, 105, 5, 'Muito bom mesmo. Trocou a torneira e ainda ajustou o registro sem cobrar.'),
 (104, 105, 3, 'Servico ok. A comunicacao antes do dia foi meio confusa.'),
 (105, 105, 5, 'Chegou no horario marcado e comecou na hora.'),
 (106, 105, 4, 'Trabalho benfeito, ficou melhor do que estava antes.'),
 (107, 105, 1, 'Cobrou a visita mesmo sem resolver nada. Achei desonesto.'),
 (108, 105, 5, 'Sem falta veio no dia combinado, mesmo chovendo muito.'),
 (109, 105, 4, 'Preco um pouco alto, mas o servico justifica.'),
 (110, 105, 5, 'Indico de olhos fechados. Honesto e caprichoso.');

-- --- 106 Cleber Matos (pintor) -- bom, mas atrasa ----------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (111, 106, 4, 'Pintura ficou linda, mas levou tres dias a mais do que o combinado.'),
 (112, 106, 5, 'Acabamento perfeito, sem respingo em lugar nenhum. Protegeu todos os moveis.'),
 (113, 106, 3, 'O trabalho e bom, o problema e o prazo. Estourou uma semana.'),
 (114, 106, 5, 'Caprichoso demais. Passou tres demaos sem eu precisar pedir.'),
 (115, 106, 2, 'Atrasou muito e ainda deixou tinta no piso da varanda.'),
 (116, 106, 4, 'Bom pintor, so precisa melhorar a pontualidade.'),
 (117, 106, 5, 'Fez a textura da sala exatamente como eu queria. Ficou impecavel.'),
 (118, 106, 3, 'Servico bom, prazo ruim. No geral compensa.'),
 (119, 106, 5, 'Nao deixou nenhuma falha na parede, nem nos cantos.'),
 (120, 106, 4, 'Preco justo e material de qualidade. So a demora que incomoda.'),
 (121, 106, 1, 'Sumiu no meio da obra e voltou duas semanas depois. Inaceitavel.'),
 (122, 106, 5, 'Trabalho de qualidade, acabamento impecavel. Recomendo.'),
 (123, 106, 3, 'Pintou bem, mas nao avisou que ia faltar no dia.'),
 (124, 106, 4, 'Organizado, cobriu tudo com lona antes de comecar.'),
 (125, 106, 5, 'Ficou melhor do que eu imaginava. Valeu cada real.'),
 (126, 106, 2, 'Combinamos duas cores e ele pintou tudo de uma so. Teve que refazer.'),
 (127, 106, 4, 'Refez a parte errada sem cobrar nada a mais. Isso conta muito.'),
 (128, 106, 5, 'pintou tudo direitinho, ficou otimoooo'),
 (129, 106, 3, 'Meia boca no prazo, bom no resultado.'),
 (130, 106, 4, 'Educado e limpo. Levou os restos de tinta embora.'),
 (131, 106, 5, 'Aplicou massa corrida e a parede ficou lisa como espelho.'),
 (132, 106, 2, 'Cobrou material que eu ja tinha comprado. Precisei discutir para descontar.');

-- --- 107 Adriana Peixoto (jardinagem) -- mista -------------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (133, 107, 5, 'Deixou o jardim irreconhecivel. Muito capricho na poda.'),
 (134, 107, 3, 'Cortou a grama bem, mas deixou os galhos empilhados no quintal.'),
 (135, 107, 4, 'Boa profissional, entende de planta de verdade.'),
 (136, 107, 2, 'Podou a arvore errada. Tive que replantar.'),
 (137, 107, 5, 'Paisagismo lindo, superou a expectativa.'),
 (138, 107, 3, 'Servico dentro do esperado, nada excepcional.'),
 (139, 107, 4, 'Pontual e caprichosa. So cobra um pouco caro pela hora.'),
 (140, 107, 1, 'Nao apareceu no dia e nao deu satisfacao nenhuma.'),
 (141, 107, 5, 'Salvou minhas roseiras que estavam morrendo. Muito conhecimento.'),
 (142, 107, 3, 'Fez o corte, mas nao levou o entulho como tinha combinado.'),
 (143, 107, 4, 'Trouxe todas as ferramentas e trabalhou o dia inteiro sem parar.'),
 (144, 107, 2, 'Deixou o jardim pela metade e disse que voltava na semana seguinte.'),
 (145, 107, 5, 'Adorei o resultado. Sugeriu plantas que combinam com a sombra do quintal.'),
 (146, 107, 3, 'Nem sempre pontual, mas o trabalho e bom.'),
 (147, 107, 4, 'Limpou o terreno inteiro em um dia. Rapida e eficiente.'),
 (148, 107, 1, 'Cobrou por hora e enrolou o servico para durar mais. Nao recomendo.'),
 (149, 107, 5, 'Sem duvida indico. Educada e muito profissional.'),
 (150, 107, 3, 'Resolveu, embora eu tenha tido que cobrar o retorno.'),
 (151, 107, 4, 'Bom trabalho de manutencao. A comunicacao por mensagem e lenta.'),
 (152, 107, 2, 'Estragou o sistema de irrigacao e nao quis assumir.'),
 (153, 107, 5, 'Chegou cedo, trabalhou direto e deixou tudo varrido.');

-- --- 108 Wellington Souza (montador) -- bom ----------------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (154, 108, 5, 'Montou o guarda-roupa de seis portas sozinho em tres horas. Impressionante.'),
 (155, 108, 4, 'Bom montador. Faltou uma peca e ele improvisou bem.'),
 (156, 108, 5, 'Rapido, limpo e levou todas as caixas embora.'),
 (157, 108, 3, 'Montou certo, mas riscou a lateral do movel.'),
 (158, 108, 5, 'Muita experiencia, resolveu sem titubear mesmo com o manual errado.'),
 (159, 108, 4, 'Pontual e educado. So demorou mais do que o previsto.'),
 (160, 108, 5, 'Instalou as prateleiras no nivel perfeito. Trabalho de precisao.'),
 (161, 108, 2, 'Montou a comoda com a gaveta invertida e nao quis voltar para arrumar.'),
 (162, 108, 5, 'Trouxe todo o material e as ferramentas adequadas.'),
 (163, 108, 4, 'Servico bem feito e preco honesto.'),
 (164, 108, 5, 'Desmontou e remontou tudo na mudanca sem quebrar nada.'),
 (165, 108, 3, 'Fez o combinado, nada alem disso.'),
 (166, 108, 5, 'Recomendo! Caprichoso e muito rapido.'),
 (167, 108, 4, 'Chegou no horario e avisou quando estava a caminho.'),
 (168, 108, 1, 'Quebrou o trilho da porta e sumiu. Nao atendeu mais.'),
 (169, 108, 5, 'Montou a mesa de escritorio e ainda organizou os cabos. Atencioso.'),
 (170, 108, 4, 'Bom trabalho. So precisa avisar antes quando vai atrasar.'),
 (171, 108, 5, 'wellington e mto bom, montou td rapidinho, vlw!'),
 (172, 108, 3, 'Deu conta do recado, mas deixou parafuso sobrando.'),
 (173, 108, 5, 'Nada a reclamar. Chamo de novo com certeza.'),
 (174, 108, 4, 'Preco justo e servico garantido. Voltou para ajustar sem cobrar.');

-- --- 109 Iranildo Ramos (pedreiro) -- reputacao baixa ------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (175, 109, 1, 'Estourou o prazo e deixou o servico parado por duas semanas.'),
 (176, 109, 2, 'O reboco ficou torto. Tive que chamar outro para corrigir.'),
 (177, 109, 1, 'Sumiu com o dinheiro do material e nao terminou a parede.'),
 (178, 109, 3, 'O trabalho e razoavel, mas a bagunca que deixa e impressionante.'),
 (179, 109, 2, 'Nao cumpriu o prazo prometido nenhuma vez.'),
 (180, 109, 1, 'Pessimo acabamento. Contrapiso desnivelado, precisei refazer tudo.'),
 (181, 109, 4, 'A churrasqueira ficou boa. So a sujeira que incomodou.'),
 (182, 109, 2, 'Cobrou adiantado e depois enrolou para comecar.'),
 (183, 109, 1, 'Nao recomendo. Servico mal feito e ainda foi mal educado quando reclamei.'),
 (184, 109, 3, 'Faz o servico, mas precisa de supervisao o tempo todo.'),
 (185, 109, 2, 'Deixou entulho na calcada por um mes. O vizinho reclamou.'),
 (186, 109, 1, 'Marcou e nao veio tres vezes. Perdi a paciencia.'),
 (187, 109, 5, 'Comigo trabalhou bem, parede reta e no prazo. Fiquei satisfeito.'),
 (188, 109, 2, 'Material de ma qualidade, usou cimento vencido.'),
 (189, 109, 3, 'Pequena reforma saiu ok, mas demorou o dobro do combinado.'),
 (190, 109, 1, 'Quebrou um cano dentro da parede e nao assumiu o conserto.'),
 (191, 109, 2, 'Preco baixo, resultado baixo. Aprendi a licao.'),
 (192, 109, 4, 'Melhorou depois que conversei. Terminou direito.'),
 (193, 109, 1, 'Nunca mais. Deixou a obra parada e sumiu do aplicativo.'),
 (194, 109, 3, 'Servico comum, nada que impressione.'),
 (195, 109, 2, 'Nao avisou que precisaria de mais material e a obra parou dois dias.');

-- --- 110 Fabio Kenji (informatica) -- reputacao alta -------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (196, 110, 5, 'Formatou o notebook e deixou mais rapido do que quando comprei.'),
 (197, 110, 5, 'Removeu o virus e ainda instalou um antivirus decente. Explicou tudo.'),
 (198, 110, 5, 'Atendimento remoto excelente, resolveu em quarenta minutos.'),
 (199, 110, 4, 'Bom tecnico. So demorou para conseguir horario.'),
 (200, 110, 5, 'Configurou a rede da loja inteira. Nunca mais caiu a internet.'),
 (201, 110, 5, 'Muito didatico, ensinou minha mae a usar o computador sem pressa.'),
 (202, 110, 5, 'Preco justo e servico garantido por noventa dias.'),
 (203, 110, 4, 'Resolveu o problema. A comunicacao por e-mail foi um pouco lenta.'),
 (204, 110, 5, 'Salvou meus arquivos que eu achava que tinha perdido. Heroi.'),
 (205, 110, 5, 'Pontual, educado e honesto. Disse que nao valia a pena trocar a peca.'),
 (206, 110, 3, 'Funcionou, mas o problema voltou em uma semana.'),
 (207, 110, 5, 'fabio e fera, resolveu td em 1h e cobrou barato'),
 (208, 110, 5, 'Nao cobrou nada porque era so uma configuracao simples. Muito honesto.'),
 (209, 110, 4, 'Bom atendimento, chegou no horario combinado.'),
 (210, 110, 5, 'Trabalho impecavel. Deixou o computador organizado e com backup automatico.'),
 (211, 110, 5, 'Sem duvida o melhor tecnico que ja chamei aqui em casa.'),
 (212, 110, 4, 'Servico bom, so achei o valor da visita um pouco alto.'),
 (213, 110, 5, 'Atendeu no domingo por videochamada e resolveu na hora.'),
 (214, 110, 2, 'Tecnico bom, mas remarcou duas vezes e eu precisava com urgencia.'),
 (215, 110, 5, 'Instalou os programas e ainda migrou tudo do computador antigo.'),
 (216, 110, 5, 'Nada a reclamar. Rapido, limpo e transparente no orcamento.');

-- --- 111 Nilton Barreto (ar-condicionado) -- bom, comunicacao ruim -----
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (217, 111, 4, 'Instalou bem, mas quase nao responde mensagem. Dificil de falar com ele.'),
 (218, 111, 5, 'Limpeza do ar-condicionado ficou impecavel, saiu ate o cheiro ruim.'),
 (219, 111, 3, 'Servico bom, comunicacao pessima. So descobri o horario na vespera.'),
 (220, 111, 5, 'Recarregou o gas e o aparelho voltou a gelar como novo.'),
 (221, 111, 4, 'Trabalho caprichado, so a demora para dar retorno que incomoda.'),
 (222, 111, 2, 'Nao avisou que nao viria. Fiquei o dia todo esperando.'),
 (223, 111, 5, 'Instalou dois splits em um dia, tudo alinhado e sem vazamento.'),
 (224, 111, 3, 'Resolveu, mas nao explicou nada do que fez.'),
 (225, 111, 4, 'Pontual no dia do servico. O problema e antes, para combinar.'),
 (226, 111, 5, 'Preco justo e trabalho limpo. Levou ate o po da furadeira.'),
 (227, 111, 1, 'Cobrou a visita, disse que voltaria com a peca e nunca mais deu noticia.'),
 (228, 111, 5, 'Muito experiente, achou o vazamento de gas que outro nao tinha visto.'),
 (229, 111, 4, 'Bom servico. Poderia melhorar no atendimento pelo telefone.'),
 (230, 111, 3, 'Fez o combinado, sem mais.'),
 (231, 111, 5, 'Chegou adiantado e terminou antes do previsto.'),
 (232, 111, 4, 'Deu garantia de um ano na instalacao. Isso vale muito.'),
 (233, 111, 2, 'Instalou torto e precisei chamar de volta. Voltou, mas demorou.'),
 (234, 111, 5, 'Nada a reclamar no servico. So o aplicativo que ele nunca ve.'),
 (235, 111, 4, 'Trabalho bem feito e material de primeira.'),
 (236, 111, 5, 'Salvou meu verao. Veio no mesmo dia que liguei.'),
 (237, 111, 3, 'O aparelho voltou a gelar, mas o barulho continuou.');

-- --- 112 Gerson Pinheiro (chaveiro) -- rapido e bem avaliado -----------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (238, 112, 5, 'Chegou em quinze minutos de madrugada. Abriu a porta sem danificar nada.'),
 (239, 112, 5, 'Rapidez impressionante. Fiquei trancado para fora e ele resolveu na hora.'),
 (240, 112, 5, 'Instalou a fechadura digital e configurou o aplicativo comigo.'),
 (241, 112, 4, 'Bom servico, so cobra taxa de urgencia salgada a noite.'),
 (242, 112, 5, 'Honesto, disse que nao precisava trocar a fechadura toda.'),
 (243, 112, 5, 'Fez as copias na hora e todas funcionaram de primeira.'),
 (244, 112, 3, 'Resolveu, mas arranhou um pouco a porta.'),
 (245, 112, 5, 'Atendimento nota dez. Educado e muito rapido.'),
 (246, 112, 4, 'Preco ok para a urgencia. Chegou rapido.'),
 (247, 112, 5, 'Nao danificou a fechadura e ainda lubrificou de graca.'),
 (248, 112, 5, 'gerson salvou minha noite, chegou rapidao e cobrou justo'),
 (249, 112, 5, 'Profissional de confianca, mostrou documento antes de entrar.'),
 (250, 112, 2, 'Demorou quase duas horas sendo que prometeu trinta minutos.'),
 (251, 112, 5, 'Trocou as fechaduras do predio inteiro em uma manha.'),
 (252, 112, 4, 'Servico bom. A copia da chave do carro ficou um pouco dura.'),
 (253, 112, 5, 'Sem duvida o mais rapido da regiao. Ja e meu chaveiro fixo.'),
 (254, 112, 5, 'Veio no feriado sem cobrar a mais. Muito atencioso.'),
 (255, 112, 4, 'Trabalho limpo e sem enrolacao.'),
 (256, 112, 5, 'Recomendo demais. Resolveu o que dois outros nao conseguiram.'),
 (257, 112, 1, 'Cobrou trezentos reais para abrir uma porta simples. Achei abusivo.'),
 (258, 112, 5, 'Chegou no horario que falou, coisa rara hoje em dia.');

-- --- 113 Marlene Duarte (cuidadora) -- reputacao muito alta ------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (259, 113, 5, 'Cuidou da minha mae com muito carinho. Paciencia de santa.'),
 (260, 113, 5, 'Pontual, atenciosa e muito organizada com os remedios.'),
 (261, 113, 5, 'Meu pai ficou a vontade com ela desde o primeiro dia. Confianca total.'),
 (262, 113, 4, 'Otima profissional. So precisou de uns dias para pegar a rotina.'),
 (263, 113, 5, 'Acompanhou minha avo no hospital a noite inteira sem reclamar.'),
 (264, 113, 5, 'Anota tudo: horario de remedio, pressao, o que comeu. Muito profissional.'),
 (265, 113, 5, 'Educada, discreta e respeitosa dentro de casa.'),
 (266, 113, 5, 'Nada a reclamar. Recomendo para qualquer familia.'),
 (267, 113, 4, 'Muito boa. O valor da diaria e um pouco acima da media.'),
 (268, 113, 5, 'Percebeu que minha mae estava com febre antes de todo mundo. Atenta.'),
 (269, 113, 5, 'Nunca faltou em seis meses. Quando precisou trocar, avisou com antecedencia.'),
 (270, 113, 5, 'Carinhosa de verdade, nao e so trabalho para ela.'),
 (271, 113, 3, 'Boa cuidadora, mas nao tem experiencia com paciente acamado.'),
 (272, 113, 5, 'Ajudou na fisioterapia e ainda animou meu pai a voltar a caminhar.'),
 (273, 113, 5, 'Profissional excelente. Sem duvida a melhor que contratamos.'),
 (274, 113, 4, 'Atenciosa e pontual. A comunicacao com a familia poderia ser mais frequente.'),
 (275, 113, 5, 'Cozinha muito bem e respeita a dieta que o medico passou.'),
 (276, 113, 5, 'Seguranca total. Sei que minha mae esta em boas maos.'),
 (277, 113, 2, 'Faltou duas vezes seguidas e eu precisei sair do trabalho.'),
 (278, 113, 5, 'Trata meu avo como se fosse da familia dela.'),
 (279, 113, 5, 'Chegou no horario todos os dias do mes. Compromisso raro.');

-- --- 114 Sidney Camargo (frete) -- reputacao baixa ---------------------
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (280, 114, 2, 'Atrasou tres horas na mudanca. O caminhao era menor do que o combinado.'),
 (281, 114, 4, 'Fez o frete direitinho e o preco foi camarada.'),
 (282, 114, 1, 'Quebrou a televisao e nao quis pagar. Disse que ja estava assim.'),
 (283, 114, 3, 'Levou tudo, mas amassou algumas caixas.'),
 (284, 114, 2, 'Cobrou a mais no final alegando que tinha mais volume. Nao combinamos isso.'),
 (285, 114, 5, 'Mudanca tranquila, embalou tudo com cuidado.'),
 (286, 114, 3, 'Servico comum. Chegou atrasado mas resolveu.'),
 (287, 114, 1, 'Nao apareceu no dia da mudanca. Tive que contratar outro as pressas.'),
 (288, 114, 2, 'Os ajudantes eram despreparados. Arrastaram o sofa pelo chao.'),
 (289, 114, 4, 'Bom servico para pequenos fretes. Rapido e sem enrolacao.'),
 (290, 114, 3, 'Levou os moveis inteiros, mas demorou o dobro do previsto.'),
 (291, 114, 2, 'Sem cuidado nenhum com os moveis. Riscou o guarda-roupa todo.'),
 (292, 114, 5, 'Dessa vez foi excelente. Pontual e caprichoso na embalagem.'),
 (293, 114, 1, 'Sumiu com uma caixa de loucas. Nunca mais apareceu.'),
 (294, 114, 3, 'Preco bom, cuidado mediano. Voce decide o que vale mais.'),
 (295, 114, 2, 'Nao trouxe cinta nem manta de protecao como tinha prometido.'),
 (296, 114, 4, 'Transportou o piano sem um arranhao. Nesse ponto foi otimo.'),
 (297, 114, 1, 'Pessimo. Atrasou, cobrou mais e ainda perdeu pecas da cama.'),
 (298, 114, 3, 'Deu para o gasto. Nao contrataria para mudanca grande.'),
 (299, 114, 2, 'Precisei ajudar a carregar porque veio sozinho.'),
 (300, 114, 4, 'Mudanca comercial feita no fim de semana como combinado. Cumpriu o prazo.');

-- --- Casos especiais para exercitar o pipeline -------------------------
-- Dado pessoal no texto: a anonimizacao de clean.py deve trocar por
-- <TELEFONE> e <EMAIL> antes de o texto chegar ao modelo.
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (301, 110, 5, 'Recomendo! Quem quiser o contato dele e 11 97654-3210, atende rapido.'),
 (302, 112, 4, 'Anota o telefone do Gerson: (11) 3456-7890. Salvou a gente duas vezes.'),
 (303, 105, 5, 'Mandei e-mail para joel.encanador@exemplo.com.br e ele respondeu na hora.'),
 (304, 101, 5, 'Meu CPF 123.456.789-00 estava errado na nota e ele corrigiu sem reclamar.');

-- Comentarios curtos demais: passam pelo filtro do SQL, mas sao descartados
-- por comentario_utilizavel (minimo de 3 caracteres). Ficam no fim da fila
-- de proposito, para nao travar o inicio do lote do worker.
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (305, 103, 5, 'ok'),
 (306, 108, 4, 'top'),
 (307, 109, 1, ':('),
 (308, 102, 3, 'bom');

-- Avaliacoes sem comentario: existem no banco e devem ser ignoradas pelo
-- extrator, que so busca avaliacao com texto.
INSERT INTO tmp_pln_avaliacao (seq, profissional_id, nota, comentario) VALUES
 (309, 101, 5, NULL), (310, 103, 4, NULL), (311, 105, 5, NULL), (312, 106, 3, NULL),
 (313, 108, 5, NULL), (314, 110, 5, NULL), (315, 111, 4, NULL), (316, 112, 5, NULL),
 (317, 113, 5, NULL), (318, 114, 2, NULL), (319, 104, 1, NULL), (320, 107, 3, NULL);

-- Datas: espalha os servicos entre fevereiro e agosto de 2026.
UPDATE tmp_pln_avaliacao
   SET dt_servico = TIMESTAMP('2026-02-02 08:00:00')
                  + INTERVAL ((seq * 13) % 190) DAY
                  + INTERVAL (seq % 9) HOUR;

-- ---------------------------------------------------------------------
-- 5. Orcamentos concluidos
--    O servico e escolhido por resto de divisao entre os servicos ativos
--    do proprio profissional, entao a avaliacao sempre aponta para um
--    servico que pertence a ele -- que e o que o SELECT do PLN exige.
-- ---------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_pln_servico_rn;
CREATE TEMPORARY TABLE tmp_pln_servico_rn AS
SELECT s.id_servico,
       s.fk_id_profissional_usuario                                                          AS profissional_id,
       ROW_NUMBER() OVER (PARTITION BY s.fk_id_profissional_usuario ORDER BY s.id_servico) - 1 AS rn,
       COUNT(*)     OVER (PARTITION BY s.fk_id_profissional_usuario)                           AS qt_servicos
  FROM servico s
 WHERE s.fk_id_profissional_usuario BETWEEN 101 AND 114
   AND s.st_ativo = 1;

INSERT INTO orcamento
    (id_orcamento, fk_id_servico, fk_id_usuario, fk_id_orcamento_status, ds_descricao,
     dt_preferido_solicitado, dt_inicio_proposto, dt_fim_proposto,
     dt_conclusao, fk_id_usuario_conclusao, ds_observacao_conclusao, dt_criacao)
SELECT 1000 + a.seq,
       sv.id_servico,
       201 + (a.seq % 36),
       (SELECT id_orcamento_status FROM orcamento_status WHERE ds_status = 'concluido'),
       'Servico contratado pelo aplicativo.',
       a.dt_servico,
       a.dt_servico,
       a.dt_servico + INTERVAL 2 HOUR,
       a.dt_servico + INTERVAL 2 HOUR,
       a.profissional_id,
       'Servico concluido e conferido com o cliente.',
       a.dt_servico - INTERVAL 7 DAY
  FROM tmp_pln_avaliacao a
  JOIN tmp_pln_servico_rn sv
    ON sv.profissional_id = a.profissional_id
   AND sv.rn = a.seq % sv.qt_servicos;

DROP TEMPORARY TABLE IF EXISTS tmp_pln_servico_rn;

-- ---------------------------------------------------------------------
-- 6. Avaliacoes
-- ---------------------------------------------------------------------
-- 6.1 Cliente avalia o profissional -- estas sao as que o PLN processa.
INSERT INTO avaliacao_reserva
    (fk_id_reserva, fk_id_avaliador_usuario, fk_id_usuario_avaliado, qt_nota, ds_comentario, dt_criacao)
SELECT 1000 + a.seq,
       201 + (a.seq % 36),
       a.profissional_id,
       a.nota,
       a.comentario,
       a.dt_servico + INTERVAL 1 DAY
  FROM tmp_pln_avaliacao a;

-- 6.2 Profissional avalia o cliente.
--     Mesmo orcamento, avaliador diferente. O SELECT do PLN filtra por
--     fk_id_usuario_avaliado = profissional, entao estas ficam de fora --
--     e servem justamente para provar que o filtro funciona.
DROP TEMPORARY TABLE IF EXISTS tmp_pln_avaliacao_cliente;
CREATE TEMPORARY TABLE tmp_pln_avaliacao_cliente
(
    seq        INT          NOT NULL,
    nota       TINYINT      NOT NULL,
    comentario VARCHAR(200) NOT NULL,
    PRIMARY KEY (seq)
);

INSERT INTO tmp_pln_avaliacao_cliente VALUES
 (  5, 5, 'Cliente organizado, deixou o espaco livre para o trabalho.'),
 ( 17, 4, 'Tudo certo, so demorou a liberar o acesso ao predio.'),
 ( 23, 5, 'Otimo cliente, pagamento em dia.'),
 ( 41, 3, 'Cliente exigente, mudou o pedido no meio do servico.'),
 ( 56, 5, 'Muito educada e prestativa. Recomendo.'),
 ( 68, 2, 'Reclamou de tudo e atrasou o pagamento.'),
 ( 79, 5, 'Casa organizada, cliente tranquilo.'),
 ( 94, 4, 'Combinou tudo certinho pelo aplicativo.'),
 (103, 5, 'Cliente parceiro, ofereceu cafe e deixou trabalhar em paz.'),
 (117, 5, 'Excelente cliente, indicou meu trabalho para os vizinhos.'),
 (126, 2, 'Nao gostou do resultado combinado e quis desconto no final.'),
 (141, 5, 'Muito atenciosa, deixou tudo preparado antes de eu chegar.'),
 (158, 4, 'Tranquilo. So o endereco que estava errado no aplicativo.'),
 (170, 5, 'Cliente pontual no pagamento e muito educado.'),
 (183, 1, 'Cliente grosseiro, nao deu condicao de trabalho.'),
 (191, 3, 'Pagou certinho, mas o local estava cheio de entulho.'),
 (205, 5, 'Otimo cliente, explicou bem o problema antes.'),
 (226, 5, 'Tudo tranquilo, acesso liberado e vaga reservada.'),
 (249, 5, 'Cliente de confianca, ja e a terceira vez que atendo.'),
 (285, 4, 'Mudanca organizada, tudo encaixotado antes de eu chegar.');

INSERT INTO avaliacao_reserva
    (fk_id_reserva, fk_id_avaliador_usuario, fk_id_usuario_avaliado, qt_nota, ds_comentario, dt_criacao)
SELECT 1000 + c.seq,
       o.fk_id_usuario_conclusao,
       o.fk_id_usuario,
       c.nota,
       c.comentario,
       o.dt_conclusao + INTERVAL 1 DAY
  FROM tmp_pln_avaliacao_cliente c
  JOIN orcamento o
    ON o.id_orcamento = 1000 + c.seq;

DROP TEMPORARY TABLE IF EXISTS tmp_pln_avaliacao_cliente;
DROP TEMPORARY TABLE IF EXISTS tmp_pln_avaliacao;

-- ---------------------------------------------------------------------
-- 7. Contadores derivados
-- ---------------------------------------------------------------------
UPDATE usuario u
  JOIN (
        SELECT fk_id_usuario_avaliado AS usuario_id,
               COUNT(*)               AS qt,
               ROUND(AVG(qt_nota), 1) AS media
          FROM avaliacao_reserva
         WHERE fk_id_reserva BETWEEN 1001 AND 1999
         GROUP BY fk_id_usuario_avaliado
       ) r ON r.usuario_id = u.id_usuario
   SET u.qt_avaliacoes      = r.qt,
       u.qt_avaliacao_geral = r.media,
       u.dt_atualizacao     = NOW();

UPDATE profissional p
  JOIN (
        SELECT s.fk_id_profissional_usuario AS profissional_id,
               COUNT(*)                     AS qt
          FROM orcamento o
          JOIN servico s ON s.id_servico = o.fk_id_servico
         WHERE o.id_orcamento BETWEEN 1001 AND 1999
         GROUP BY s.fk_id_profissional_usuario
       ) c ON c.profissional_id = p.usuario_id
   SET p.qt_servicos_concluido = c.qt,
       p.dt_atualizacao        = NOW();

UPDATE servico s
  JOIN (
        SELECT o.fk_id_servico            AS servico_id,
               COUNT(*)                   AS qt,
               ROUND(AVG(a.qt_nota), 2)   AS media
          FROM orcamento o
          JOIN avaliacao_reserva a
            ON a.fk_id_reserva = o.id_orcamento
           AND a.fk_id_usuario_avaliado <> o.fk_id_usuario
         WHERE o.id_orcamento BETWEEN 1001 AND 1999
         GROUP BY o.fk_id_servico
       ) r ON r.servico_id = s.id_servico
   SET s.qt_reservado       = r.qt,
       s.nr_avaliacao_geral = r.media,
       s.dt_atualizacao     = NOW();

-- ---------------------------------------------------------------------
-- 8. Enfileira os profissionais para reindexacao no Elasticsearch
-- ---------------------------------------------------------------------
INSERT INTO search_outbox (fk_profissional_id, dt_criacao, dt_processamento, nr_tentativas)
SELECT p.usuario_id, NOW(3), NULL, 0
  FROM profissional p
 WHERE p.usuario_id BETWEEN 101 AND 114
   AND NOT EXISTS (
       SELECT 1 FROM search_outbox pendente
        WHERE pendente.fk_profissional_id = p.usuario_id
          AND pendente.dt_processamento IS NULL
   );

COMMIT;