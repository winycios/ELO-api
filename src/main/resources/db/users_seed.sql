-- primeiro os usuarios
INSERT INTO database_elo.usuario (id_usuario, nm_nome, nm_sobrenome, ds_email, senha, dt_criacao, tel_celular,
                                  tel_whats, qt_avaliacao_geral, qt_avaliacoes, uri_perfil, st_habilitado)
VALUES (1, 'profissional', 'alves', 'profissional@gmail.com',
        '$2a$10$ZKgvowvdRsZHd71Wc7YVgebPXSDcW3PfZT/YNYr4V1ihjkqea4g/q', '2026-07-14 22:19:51', '11984675735',
        '11984675735', null, null, null, 0);
INSERT INTO database_elo.usuario (id_usuario, nm_nome, nm_sobrenome, ds_email, senha, dt_criacao, tel_celular,
                                  tel_whats, qt_avaliacao_geral, qt_avaliacoes, uri_perfil, st_habilitado)
VALUES (2, 'usuario', 'alves', 'usuario@gmail.com', '$2a$10$5w.DrPjLYllsRokkFrLbQuqJiUf4kqA8nmMYdf9PkhfbGBBtCyuWS',
        '2026-07-14 22:20:06', '11984675735', '11984675735', null, null, null, 1);
INSERT INTO database_elo.usuario (id_usuario, nm_nome, nm_sobrenome, ds_email, senha, dt_criacao, tel_celular,
                                  tel_whats, qt_avaliacao_geral, qt_avaliacoes, uri_perfil, st_habilitado)
VALUES (3, 'ambos', 'alves', 'ambos@gmail.com', '$2a$10$JEBTt/uw2676R09VkAoBjuMd6wjGp24hkZO7X.ckgnMwqQEc7dWPG',
        '2026-07-14 22:20:20', '11984675735', '11984675735', null, null, null, 1);

-- segundo os profissionais
INSERT INTO database_elo.profissional (usuario_id, dt_criacao, qt_resposta_geral, st_disponivel, ds_apresentacao,
                                       uri_perfil, ds_especialidades, st_habilitado)
VALUES (1, '2026-07-14 22:19:51', null, null, null, null, null, 1);
INSERT INTO database_elo.profissional (usuario_id, dt_criacao, qt_resposta_geral, st_disponivel, ds_apresentacao,
                                       uri_perfil, ds_especialidades, st_habilitado)
VALUES (2, '2026-07-14 22:20:06', null, null, null, null, null, 0);
INSERT INTO database_elo.profissional (usuario_id, dt_criacao, qt_resposta_geral, st_disponivel, ds_apresentacao,
                                       uri_perfil, ds_especialidades, st_habilitado)
VALUES (3, '2026-07-14 22:20:20', null, null, null, null, null, 1);
