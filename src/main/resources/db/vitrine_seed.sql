USE `database_elo`;

DELIMITER $$

DROP PROCEDURE IF EXISTS `seed_vitrine`$$

CREATE PROCEDURE `seed_vitrine`()
BEGIN
    DECLARE v_numero INT DEFAULT 1;
    DECLARE v_total_categorias INT DEFAULT 0;
    DECLARE v_categoria_offset INT DEFAULT 0;
    DECLARE v_categoria_id INT;
    DECLARE v_profissional_id INT;
    DECLARE v_publicacao_id INT;
    DECLARE v_descricao VARCHAR(100);
    DECLARE v_imagem_url VARCHAR(500);

    SELECT COUNT(*)
      INTO v_total_categorias
      FROM categoria_especifica;

    IF v_total_categorias = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cadastre ao menos uma categoria especifica antes de executar o seed.';
    END IF;

    IF (SELECT COUNT(*) FROM profissional WHERE usuario_id IN (1, 3)) <> 2 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Os profissionais 1 e 3 precisam existir antes de executar o seed.';
    END IF;

    START TRANSACTION;

    WHILE v_numero <= 40 DO
        SET v_profissional_id = CASE
            WHEN v_numero MOD 2 = 1 THEN 1
            ELSE 3
        END;
        SET v_categoria_offset = (v_numero - 1) MOD v_total_categorias;

        SELECT id_categoria_especifica
          INTO v_categoria_id
          FROM categoria_especifica
         ORDER BY id_categoria_especifica
         LIMIT v_categoria_offset, 1;

        SET v_descricao = CASE (v_numero - 1) MOD 10
            WHEN 0 THEN 'Instalacao eletrica concluida com seguranca e acabamento.'
            WHEN 1 THEN 'Reparo hidraulico finalizado e ambiente pronto para uso.'
            WHEN 2 THEN 'Pintura renovada com preparacao completa das paredes.'
            WHEN 3 THEN 'Jardim revitalizado com poda, limpeza e novos detalhes.'
            WHEN 4 THEN 'Montagem realizada com cuidado e excelente acabamento.'
            WHEN 5 THEN 'Manutencao preventiva concluida com todos os testes.'
            WHEN 6 THEN 'Reforma entregue: mais conforto e funcionalidade ao espaco.'
            WHEN 7 THEN 'Limpeza profissional concluida em todos os ambientes.'
            WHEN 8 THEN 'Novo projeto executado conforme o combinado com o cliente.'
            ELSE 'Mais um servico finalizado com qualidade e atencao aos detalhes.'
        END;

        INSERT INTO publicacao (
            fk_profissional_usuario_id,
            fk_categoria_Especifica_id,
            ds_publicacao,
            dt_publicacao,
            st_ativo
        ) VALUES (
            v_profissional_id,
            v_categoria_id,
            v_descricao,
            DATE_SUB(NOW(), INTERVAL v_numero HOUR),
            1
        );

        SET v_publicacao_id = LAST_INSERT_ID();

        SET v_imagem_url = CASE (v_numero - 1) MOD 10
            WHEN 0 THEN 'https://images.unsplash.com/photo-1621905252507-b35492cc74b4?auto=format&fit=crop&w=1200&q=80'
            WHEN 1 THEN 'https://images.unsplash.com/photo-1585704032915-c3400ca199e7?auto=format&fit=crop&w=1200&q=80'
            WHEN 2 THEN 'https://images.unsplash.com/photo-1505798577917-a65157d3320a?auto=format&fit=crop&w=1200&q=80'
            WHEN 3 THEN 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?auto=format&fit=crop&w=1200&q=80'
            WHEN 4 THEN 'https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=1200&q=80'
            WHEN 5 THEN 'https://images.unsplash.com/photo-1504917595217-d4dc5ebe6122?auto=format&fit=crop&w=1200&q=80'
            WHEN 6 THEN 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?auto=format&fit=crop&w=1200&q=80'
            WHEN 7 THEN 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=1200&q=80'
            WHEN 8 THEN 'https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=1200&q=80'
            ELSE 'https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=1200&q=80'
        END;

        INSERT INTO publicacao_imagem (fk_publicacao_id, url_imagem, nr_ordem)
        VALUES (v_publicacao_id, v_imagem_url, 1);

        -- Algumas publicacoes possuem uma segunda foto para testar carrossel/ordenacao.
        IF v_numero MOD 4 = 0 THEN
            INSERT INTO publicacao_imagem (fk_publicacao_id, url_imagem, nr_ordem)
            VALUES (
                v_publicacao_id,
                'https://images.unsplash.com/photo-1505798577917-a65157d3320a?auto=format&fit=crop&w=1200&q=80',
                2
            );
        END IF;

        SET v_numero = v_numero + 1;
    END WHILE;

    COMMIT;
END$$

CALL `seed_vitrine`()$$
DROP PROCEDURE `seed_vitrine`$$

DELIMITER ;

SELECT p.id_publicacao,
       p.fk_profissional_usuario_id,
       p.fk_categoria_Especifica_id,
       p.ds_publicacao,
       p.dt_publicacao,
       COUNT(i.id_publicacao_Imagem) AS quantidade_imagens
  FROM publicacao p
  JOIN publicacao_imagem i ON i.fk_publicacao_id = p.id_publicacao
 WHERE p.fk_profissional_usuario_id IN (1, 3)
 GROUP BY p.id_publicacao,
          p.fk_profissional_usuario_id,
          p.fk_categoria_Especifica_id,
          p.ds_publicacao,
          p.dt_publicacao
 ORDER BY p.dt_publicacao DESC, p.id_publicacao DESC
 LIMIT 40;
