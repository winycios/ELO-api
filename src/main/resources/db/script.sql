-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE ='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema database_elo
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema database_elo
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `database_elo` DEFAULT CHARACTER SET utf8mb3;
USE `database_elo`;

-- -----------------------------------------------------
-- Table `database_elo`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`usuario`
(
    `id_usuario`         INT           NOT NULL AUTO_INCREMENT,
    `nm_nome`            VARCHAR(45)   NOT NULL,
    `nm_sobrenome`       VARCHAR(45)   NULL DEFAULT NULL,
    `ds_email`           VARCHAR(200)  NOT NULL,
    `senha`              VARCHAR(200)  NOT NULL,
    `dt_criacao`         DATETIME      NULL DEFAULT NULL,
    `tel_celular`        VARCHAR(45)   NULL DEFAULT NULL,
    `tel_whats`          VARCHAR(45)   NULL DEFAULT NULL,
    `qt_avaliacao_geral` DECIMAL(2, 1) NULL DEFAULT NULL,
    `qt_avaliacoes`      INT           NULL DEFAULT '0',
    `uri_perfil`         VARCHAR(255)  NULL DEFAULT NULL,
    `st_habilitado`      TINYINT(1)    NOT NULL,
    PRIMARY KEY (`id_usuario`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 4
    DEFAULT CHARACTER SET = utf8mb3;

CREATE UNIQUE INDEX `ds_email_UNIQUE` ON `database_elo`.`usuario` (`ds_email` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`profissional`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`profissional`
(
    `usuario_id`        INT          NOT NULL,
    `dt_criacao`        DATETIME     NULL DEFAULT NULL,
    `qt_resposta_geral` INT          NULL DEFAULT NULL,
    `st_disponivel`     TINYINT      NULL DEFAULT NULL,
    `ds_apresentacao`   VARCHAR(200) NULL DEFAULT NULL,
    `uri_perfil`        VARCHAR(200) NULL DEFAULT NULL,
    `ds_especialidades` VARCHAR(200) NULL DEFAULT NULL,
    `st_habilitado`     TINYINT(1)   NOT NULL,
    PRIMARY KEY (`usuario_id`),
    CONSTRAINT `fk_Profissional_Usuario0`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `database_elo`.`usuario` (`id_usuario`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Profissional_Usuario_idx` ON `database_elo`.`profissional` (`usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`area_atendimento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`area_atendimento`
(
    `id_area_Atendimento`        INT         NOT NULL AUTO_INCREMENT,
    `fk_profissional_usuario_id` INT         NOT NULL,
    `nr_latitude`                DOUBLE      NULL DEFAULT NULL,
    `nr_longitude`               DOUBLE      NULL DEFAULT NULL,
    `nr_raio`                    INT         NULL DEFAULT NULL,
    `nm_cidade`                  VARCHAR(45) NULL DEFAULT NULL,
    `nm_estado`                  VARCHAR(45) NULL DEFAULT NULL,
    `nm_bairro`                  VARCHAR(45) NULL DEFAULT NULL,
    `dt_criacao`                 DATETIME    NULL DEFAULT NULL,
    PRIMARY KEY (`id_area_Atendimento`),
    CONSTRAINT `fk_Area_Atendimento_Profissional1`
    FOREIGN KEY (`fk_profissional_usuario_id`)
    REFERENCES `database_elo`.`profissional` (`usuario_id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Area_Atendimento_Profissional1_idx` ON `database_elo`.`area_atendimento` (`fk_profissional_usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`reserva_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`reserva_status`
(
    `id_reserva_status` INT          NOT NULL AUTO_INCREMENT,
    `ds_status`         VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id_reserva_status`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `database_elo`.`categoria_geral`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`categoria_geral`
(
    `id_categoria_geral` INT          NOT NULL AUTO_INCREMENT,
    `nm_categoria`       VARCHAR(100) NULL DEFAULT NULL,
    `ds_icon`            VARCHAR(50)  NULL DEFAULT NULL,
    PRIMARY KEY (`id_categoria_geral`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 41
    DEFAULT CHARACTER SET = utf8mb3;

CREATE UNIQUE INDEX `uk_categoria_geral_nome` ON `database_elo`.`categoria_geral` (`nm_categoria` ASC) VISIBLE;

CREATE INDEX `categoria_geral_id_idx` ON `database_elo`.`categoria_geral` (`id_categoria_geral` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`categoria_especifica`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`categoria_especifica`
(
    `id_categoria_especifica` INT          NOT NULL AUTO_INCREMENT,
    `fk_id_categoria_geral`   INT          NOT NULL,
    `nm_categoria_especifica` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id_categoria_especifica`),
    CONSTRAINT `fk_categoria_geral_id`
    FOREIGN KEY (`fk_id_categoria_geral`)
    REFERENCES `database_elo`.`categoria_geral` (`id_categoria_geral`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 62
    DEFAULT CHARACTER SET = utf8mb3;

CREATE UNIQUE INDEX `uk_categoria_especifica` ON `database_elo`.`categoria_especifica` (`fk_id_categoria_geral` ASC, `nm_categoria_especifica` ASC) VISIBLE;

CREATE INDEX `fk_categoria_geral_id_idx` ON `database_elo`.`categoria_especifica` (`fk_id_categoria_geral` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`servico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`servico`
(
    `id_servico`                 INT                           NOT NULL AUTO_INCREMENT,
    `fk_id_profissional_usuario` INT                           NOT NULL,
    `fk_id_categoria_especifica` INT                           NOT NULL,
    `ds_descricao`               VARCHAR(200)                  NULL DEFAULT NULL,
    `vl_servico`                 DOUBLE                        NULL DEFAULT NULL,
    `ds_tag`                     VARCHAR(200)                  NULL DEFAULT NULL,
    `tp_execucao`                ENUM ('presencial', 'remoto') NULL DEFAULT NULL,
    `st_ativo`                   TINYINT(1)                    NULL DEFAULT NULL,
    `nr_tempo_experiencia`       INT                           NULL DEFAULT NULL,
    PRIMARY KEY (`id_servico`),
    CONSTRAINT `fk_Servico_Categoria_Especifica1`
    FOREIGN KEY (`fk_id_categoria_especifica`)
    REFERENCES `database_elo`.`categoria_especifica` (`id_categoria_especifica`),
    CONSTRAINT `fk_Servico_Profissional1`
    FOREIGN KEY (`fk_id_profissional_usuario`)
    REFERENCES `database_elo`.`profissional` (`usuario_id`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Servico_Profissional_idx` ON `database_elo`.`servico` (`fk_id_profissional_usuario` ASC) VISIBLE;

CREATE INDEX `fk_Servico_Categoria_Especifica1_idx` ON `database_elo`.`servico` (`fk_id_categoria_especifica` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`reserva`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`reserva`
(
    `id_reserva`                 INT          NOT NULL AUTO_INCREMENT,
    `fk_servico_id`              INT          NOT NULL,
    `fk_usuario_id`              INT          NOT NULL,
    `fk_reserva_Status_id`       INT          NOT NULL,
    `dt_reserva`                 DATETIME     NULL DEFAULT NULL,
    `ds_descricao`               VARCHAR(100) NULL DEFAULT NULL,
    `ds_observacao_profissional` VARCHAR(200) NULL DEFAULT NULL,
    `dt_inicio`                  DATETIME     NULL DEFAULT NULL,
    `dt_fim`                     DATETIME     NULL DEFAULT NULL,
    `ds_endereco`                VARCHAR(200) NULL DEFAULT NULL,
    PRIMARY KEY (`id_reserva`),
    CONSTRAINT `fk_Reserva_Reserva_Status1`
    FOREIGN KEY (`fk_reserva_Status_id`)
    REFERENCES `database_elo`.`reserva_status` (`id_reserva_status`),
    CONSTRAINT `fk_Reserva_Servico1`
    FOREIGN KEY (`fk_servico_id`)
    REFERENCES `database_elo`.`servico` (`id_servico`),
    CONSTRAINT `fk_Reserva_Usuario1`
    FOREIGN KEY (`fk_usuario_id`)
    REFERENCES `database_elo`.`usuario` (`id_usuario`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Reserva_Servico1_idx` ON `database_elo`.`reserva` (`fk_servico_id` ASC) VISIBLE;

CREATE INDEX `fk_Reserva_Usuario1_idx` ON `database_elo`.`reserva` (`fk_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Reserva_Reserva_Status1_idx` ON `database_elo`.`reserva` (`fk_reserva_Status_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`avaliacao_reserva`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`avaliacao_reserva`
(
    `idAvaliacao_Reserva`     INT          NOT NULL AUTO_INCREMENT,
    `fk_reserva_id`           INT          NOT NULL,
    `fk_avaliador_usuario_id` INT          NOT NULL,
    `fk_usuario_avaliado_id`  INT          NOT NULL,
    `qt_nota`                 TINYINT      NULL DEFAULT NULL,
    `ds_comentario`           VARCHAR(200) NULL DEFAULT NULL,
    `observacao_profisional`  VARCHAR(200) NULL DEFAULT NULL,
    `distancia`               DOUBLE       NULL DEFAULT NULL,
    `ds_endereco`             VARCHAR(45)  NULL DEFAULT NULL,
    PRIMARY KEY (`idAvaliacao_Reserva`),
    CONSTRAINT `fk_Avaliacao_Reserva_Reserva1`
    FOREIGN KEY (`fk_reserva_id`)
    REFERENCES `database_elo`.`reserva` (`id_reserva`),
    CONSTRAINT `fk_Avaliacao_Reserva_Usuario1`
    FOREIGN KEY (`fk_avaliador_usuario_id`)
    REFERENCES `database_elo`.`usuario` (`id_usuario`),
    CONSTRAINT `fk_Avaliacao_Reserva_Usuario2`
    FOREIGN KEY (`fk_usuario_avaliado_id`)
    REFERENCES `database_elo`.`usuario` (`id_usuario`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Avaliacao_Reserva_Reserva1_idx` ON `database_elo`.`avaliacao_reserva` (`fk_reserva_id` ASC) VISIBLE;

CREATE INDEX `fk_Avaliacao_Reserva_Usuario1_idx` ON `database_elo`.`avaliacao_reserva` (`fk_avaliador_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Avaliacao_Reserva_Usuario2_idx` ON `database_elo`.`avaliacao_reserva` (`fk_usuario_avaliado_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`endereco_usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`endereco_usuario`
(
    `id_endereco_usuario` INT                                             NOT NULL AUTO_INCREMENT,
    `nm_rua`              VARCHAR(200)                                    NULL DEFAULT NULL,
    `nm_complemento`      VARCHAR(45)                                     NULL DEFAULT NULL,
    `nm_bairro`           VARCHAR(45)                                     NULL DEFAULT NULL,
    `nm_cidade`           VARCHAR(45)                                     NULL DEFAULT NULL,
    `nm_estado`           VARCHAR(2)                                      NULL DEFAULT NULL,
    `nr_cep`              VARCHAR(10)                                     NULL DEFAULT NULL,
    `st_tipo`             ENUM ('casa', 'empresa', 'comercial', 'remoto') NULL DEFAULT NULL,
    `dt_criacao`          DATETIME                                        NULL DEFAULT NULL,
    `st_principal`        TINYINT                                         NULL DEFAULT NULL,
    `nr_latitude`         DOUBLE                                          NULL DEFAULT NULL,
    `nr_longitude`        DOUBLE                                          NULL DEFAULT NULL,
    `fk_usuario_id`       INT                                             NOT NULL,
    `st_ativo`            TINYINT(1)                                      NULL DEFAULT NULL,
    `nm_apelido`          VARCHAR(80)                                     NULL DEFAULT NULL,
    `nr_rua`              INT                                             NULL DEFAULT NULL,
    PRIMARY KEY (`id_endereco_usuario`),
    CONSTRAINT `fk_Endereco_Usuario1`
    FOREIGN KEY (`fk_usuario_id`)
    REFERENCES `database_elo`.`usuario` (`id_usuario`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Endereco_Usuario1_idx` ON `database_elo`.`endereco_usuario` (`fk_usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`publicacao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`publicacao`
(
    `id_publicacao`              INT          NOT NULL AUTO_INCREMENT,
    `fk_profissional_usuario_id` INT          NOT NULL,
    `fk_categoria_Especifica_id` INT          NOT NULL,
    `ds_publicacao`              VARCHAR(100) NULL DEFAULT NULL,
    `dt_publicacao`              DATETIME     NULL DEFAULT NULL,
    `st_ativo`                   TINYINT      NULL DEFAULT NULL,
    PRIMARY KEY (`id_publicacao`),
    CONSTRAINT `fk_Publicacao_Categoria_Especifica1`
    FOREIGN KEY (`fk_categoria_Especifica_id`)
    REFERENCES `database_elo`.`categoria_especifica` (`id_categoria_especifica`),
    CONSTRAINT `fk_Publicacao_Profissional1`
    FOREIGN KEY (`fk_profissional_usuario_id`)
    REFERENCES `database_elo`.`profissional` (`usuario_id`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 42
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Publicacao_Profissional1_idx` ON `database_elo`.`publicacao` (`fk_profissional_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Publicacao_Categoria_Especifica1_idx` ON `database_elo`.`publicacao` (`fk_categoria_Especifica_id` ASC) VISIBLE;

CREATE INDEX `idx_publicacao_feed` ON `database_elo`.`publicacao` (`st_ativo` ASC, `dt_publicacao` DESC, `id_publicacao` DESC) VISIBLE;

CREATE INDEX `idx_publicacao_feed_categoria` ON `database_elo`.`publicacao` (`fk_categoria_Especifica_id` ASC,
                                                                             `st_ativo` ASC, `dt_publicacao` DESC,
                                                                             `id_publicacao` DESC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`publicacao_comentario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`publicacao_comentario`
(
    `id_publicacao_comentario` INT          NOT NULL AUTO_INCREMENT,
    `fk_publicacao_id`         INT          NOT NULL,
    `fk_usuario_id`            INT          NOT NULL,
    `fk_comentario_pai`        INT          NULL DEFAULT NULL,
    `ds_comentario`            VARCHAR(200) NULL DEFAULT NULL,
    `dt_comentario`            DATETIME     NULL DEFAULT NULL,
    `st_ativo`                 TINYINT      NULL DEFAULT NULL,
    PRIMARY KEY (`id_publicacao_comentario`),
    CONSTRAINT `fk_Publicacao_Comentario_Publicacao1`
    FOREIGN KEY (`fk_publicacao_id`)
    REFERENCES `database_elo`.`publicacao` (`id_publicacao`),
    CONSTRAINT `fk_Publicacao_Comentario_Publicacao_Comentario1`
    FOREIGN KEY (`fk_comentario_pai`)
    REFERENCES `database_elo`.`publicacao_comentario` (`id_publicacao_comentario`),
    CONSTRAINT `fk_Publicacao_Comentario_Usuario1`
    FOREIGN KEY (`fk_usuario_id`)
    REFERENCES `database_elo`.`usuario` (`id_usuario`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 7
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Publicacao_Comentario_Usuario1_idx` ON `database_elo`.`publicacao_comentario` (`fk_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Publicacao_Comentario_Publicacao_Comentario_idx` ON `database_elo`.`publicacao_comentario` (`fk_comentario_pai` ASC) INVISIBLE;

CREATE INDEX `fk_Publicacao_Comentario_Publicacao_idx` ON `database_elo`.`publicacao_comentario` (`fk_publicacao_id` ASC, `dt_comentario` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`publicacao_curtida`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`publicacao_curtida`
(
    `id_publicacao` INT      NOT NULL,
    `id_usuario`    INT      NOT NULL,
    `dt_curtida`    DATETIME NULL DEFAULT NULL,
    PRIMARY KEY (`id_publicacao`, `id_usuario`),
    CONSTRAINT `fk_Publicacao_Curtida_Publicacao1`
    FOREIGN KEY (`id_publicacao`)
    REFERENCES `database_elo`.`publicacao` (`id_publicacao`),
    CONSTRAINT `fk_Publicacao_Curtida_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `database_elo`.`usuario` (`id_usuario`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Publicacao_Curtida_Usuario1_idx` ON `database_elo`.`publicacao_curtida` (`id_usuario` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`publicacao_imagem`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`publicacao_imagem`
(
    `id_publicacao_Imagem` INT          NOT NULL AUTO_INCREMENT,
    `fk_publicacao_id`     INT          NOT NULL,
    `url_imagem`           VARCHAR(500) NULL DEFAULT NULL,
    `nr_ordem`             INT          NULL DEFAULT NULL,
    PRIMARY KEY (`id_publicacao_Imagem`),
    CONSTRAINT `fk_Publicacao_Imagem_Publicacao1`
    FOREIGN KEY (`fk_publicacao_id`)
    REFERENCES `database_elo`.`publicacao` (`id_publicacao`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 52
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Publicacao_Imagem_Publicacao1_idx` ON `database_elo`.`publicacao_imagem` (`fk_publicacao_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`reserva_custos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`reserva_custos`
(
    `id_reserva_custos` INT         NOT NULL AUTO_INCREMENT,
    `fk_reserva_id`     INT         NOT NULL,
    `ds_descricao`      VARCHAR(45) NULL DEFAULT NULL,
    `vl_valor`          DOUBLE      NULL DEFAULT NULL,
    PRIMARY KEY (`id_reserva_custos`),
    CONSTRAINT `fk_Reserva_Custos_Reserva1`
    FOREIGN KEY (`fk_reserva_id`)
    REFERENCES `database_elo`.`reserva` (`id_reserva`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Reserva_Custos_Reserva1_idx` ON `database_elo`.`reserva_custos` (`fk_reserva_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`reserva_endereco`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`reserva_endereco`
(
    `id_reserva_endereco` INT                                               NOT NULL AUTO_INCREMENT,
    `fk_reserva_id`       INT                                               NOT NULL,
    `tp_endereco`         ENUM ('execucao', 'retirada', 'entrega')          NULL DEFAULT NULL,
    `nm_rua`              VARCHAR(45)                                       NULL DEFAULT NULL,
    `nm_complemento`      VARCHAR(45)                                       NULL DEFAULT NULL,
    `nm_bairro`           VARCHAR(45)                                       NULL DEFAULT NULL,
    `nm_cidade`           VARCHAR(45)                                       NULL DEFAULT NULL,
    `nm_estado`           VARCHAR(45)                                       NULL DEFAULT NULL,
    `nr_cep`              VARCHAR(45)                                       NULL DEFAULT NULL,
    `tp_execucao`         ENUM ('presencial', 'remoto', 'retirada_entrega') NOT NULL,
    PRIMARY KEY (`id_reserva_endereco`),
    CONSTRAINT `fk_Reserva_endereco_Reserva1`
    FOREIGN KEY (`fk_reserva_id`)
    REFERENCES `database_elo`.`reserva` (`id_reserva`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Reserva_endereco_Reserva1_idx` ON `database_elo`.`reserva_endereco` (`fk_reserva_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`servico_disponibilidade`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`servico_disponibilidade`
(
    `id_servico_disponibilidade` INT      NOT NULL AUTO_INCREMENT,
    `fk_id_servico`              INT      NOT NULL,
    `nr_dia_semana`              INT      NULL DEFAULT NULL,
    `hr_inicio`                  TIME     NULL DEFAULT NULL,
    `hr_fim`                     TIME     NULL DEFAULT NULL,
    `st_ativo`                   TINYINT  NULL DEFAULT NULL,
    `dt_criacao`                 DATETIME NULL DEFAULT NULL,
    PRIMARY KEY (`id_servico_disponibilidade`),
    CONSTRAINT `fk_Servico_Disponibilidade_Servico1`
    FOREIGN KEY (`fk_id_servico`)
    REFERENCES `database_elo`.`servico` (`id_servico`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 22
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Servico_Disponibilidade_Servico1_idx` ON `database_elo`.`servico_disponibilidade` (`fk_id_servico` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`servico_imagem`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`servico_imagem`
(
    `id_servico_imagem` INT          NOT NULL AUTO_INCREMENT,
    `fk_id_servico`     INT          NOT NULL,
    `url_imagem`        VARCHAR(500) NULL DEFAULT NULL,
    `nr_ordem`          INT          NULL DEFAULT NULL,
    PRIMARY KEY (`id_servico_imagem`),
    CONSTRAINT `fk_Servico_Imagem_Servico`
    FOREIGN KEY (`fk_id_servico`)
    REFERENCES `database_elo`.`servico` (`id_servico`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;

CREATE INDEX `fk_Servico_Imagem_Servico_idx` ON `database_elo`.`servico_imagem` (`fk_id_servico` ASC) VISIBLE;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;