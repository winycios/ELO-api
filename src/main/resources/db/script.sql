-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema database_elo
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema database_elo
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `database_elo` DEFAULT CHARACTER SET utf8 ;
USE `database_elo` ;

-- -----------------------------------------------------
-- Table `database_elo`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Usuario` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `nm_nome` VARCHAR(45) NOT NULL,
  `nm_sobrenome` VARCHAR(45) NULL,
  `ds_email` VARCHAR(200) NOT NULL,
  `senha` VARCHAR(200) NOT NULL,
  `dt_criacao` DATETIME NULL,
  `tel_celular` VARCHAR(45) NULL,
  `tel_whats` VARCHAR(45) NULL,
  PRIMARY KEY (`id_usuario`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `ds_email_UNIQUE` ON `database_elo`.`Usuario` (`ds_email` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Cliente` (
  `usuario_id` INT NOT NULL,
  `dt_criacao` DATETIME NULL,
  `st_status` VARCHAR(45) NULL,
  PRIMARY KEY (`usuario_id`),
  CONSTRAINT `fk_Cliente_Usuario`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `database_elo`.`Usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Cliente_Usuario_idx` ON `database_elo`.`Cliente` (`usuario_id` ASC) INVISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Endereco_Cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Endereco_Cliente` (
  `id_endereco_cliente` INT NOT NULL AUTO_INCREMENT,
  `nm_rua` VARCHAR(200) NULL,
  `nm_complemento` VARCHAR(45) NULL,
  `nm_bairro` VARCHAR(45) NULL,
  `nm_cidade` VARCHAR(45) NULL,
  `nm_estado` VARCHAR(2) NULL,
  `nr_cep` VARCHAR(10) NULL,
  `st_tipo` ENUM("casa", "empresa", "comercial", "remoto") NULL,
  `dt_criacao` DATETIME NULL,
  `st_principal` TINYINT NULL,
  `nr_latitude` DOUBLE NULL,
  `nr_longitude` DOUBLE NULL,
  `fk_cliente_usuario_id` INT NOT NULL,
  PRIMARY KEY (`id_endereco_cliente`),
  CONSTRAINT `fk_Endereco_Usuario_Cliente1`
    FOREIGN KEY (`fk_cliente_usuario_id`)
    REFERENCES `database_elo`.`Cliente` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Endereco_Usuario_Cliente1_idx` ON `database_elo`.`Endereco_Cliente` (`fk_cliente_usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Profissional`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Profissional` (
  `usuario_id` INT NOT NULL,
  `dt_criacao` DATETIME NULL,
  `qt_servicos` INT NULL,
  `qt_avalicacao_geral` DECIMAL(2,1) NULL,
  `qt_resposta_geral` INT NULL,
  `st_disponivel` TINYINT NULL,
  `ds_apresentacao` VARCHAR(200) NULL,
  `uri_perfil` VARCHAR(200) NULL,
  `ds_especialidades` VARCHAR(200) NULL,
  PRIMARY KEY (`usuario_id`),
  CONSTRAINT `fk_Cliente_Usuario0`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `database_elo`.`Usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Cliente_Usuario_idx` ON `database_elo`.`Profissional` (`usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Area_Atendimento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Area_Atendimento` (
  `id_area_Atendimento` INT NOT NULL AUTO_INCREMENT,
  `fk_profissional_usuario_id` INT NOT NULL,
  `nr_latitude` DOUBLE NULL,
  `nr_longitude` DOUBLE NULL,
  `nr_raio` INT NULL,
  `nm_cidade` VARCHAR(45) NULL,
  `nm_estado` VARCHAR(45) NULL,
  `nm_bairro` VARCHAR(45) NULL,
  `dt_criacao` DATETIME NULL,
  PRIMARY KEY (`id_area_Atendimento`),
  CONSTRAINT `fk_Area_Atendimento_Profissional1`
    FOREIGN KEY (`fk_profissional_usuario_id`)
    REFERENCES `database_elo`.`Profissional` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Area_Atendimento_Profissional1_idx` ON `database_elo`.`Area_Atendimento` (`fk_profissional_usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Categoria_Geral`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Categoria_Geral` (
  `id_categoria_Geral` INT NOT NULL AUTO_INCREMENT,
  `nm_categoria` VARCHAR(100) NULL,
  PRIMARY KEY (`id_categoria_Geral`))
ENGINE = InnoDB;

CREATE INDEX `categoria_geral_id_idx` ON `database_elo`.`Categoria_Geral` (`id_categoria_Geral` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Categoria_Especifica`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Categoria_Especifica` (
  `id_categoria_especifica` INT NOT NULL AUTO_INCREMENT,
  `fk_id_categoria_geral` INT NOT NULL,
  `nm_categoria_especifica` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_categoria_especifica`),
  CONSTRAINT `fk_categoria_geral_id`
    FOREIGN KEY (`fk_id_categoria_geral`)
    REFERENCES `database_elo`.`Categoria_Geral` (`id_categoria_Geral`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_categoria_geral_id_idx` ON `database_elo`.`Categoria_Especifica` (`fk_id_categoria_geral` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Servico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Servico` (
  `id_servico` INT NOT NULL AUTO_INCREMENT,
  `fk_profissional_usuario_id` INT NOT NULL,
  `Categoria_Especifica_id_categoria_especifica` INT NOT NULL,
  `ds_descricao` VARCHAR(200) NULL,
  `vl_inicial` DOUBLE NULL,
  `vl_final` DOUBLE NULL,
  `ds_tag` VARCHAR(200) NULL,
  `ds_url_imagem` VARCHAR(400) NULL,
  `tp_execucao` ENUM('presencial', 'remoto') NULL,
  PRIMARY KEY (`id_servico`),
  CONSTRAINT `fk_Servico_Profissional1`
    FOREIGN KEY (`fk_profissional_usuario_id`)
    REFERENCES `database_elo`.`Profissional` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Servico_Categoria_Especifica1`
    FOREIGN KEY (`Categoria_Especifica_id_categoria_especifica`)
    REFERENCES `database_elo`.`Categoria_Especifica` (`id_categoria_especifica`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Servico_Profissional_idx` ON `database_elo`.`Servico` (`fk_profissional_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Servico_Categoria_Especifica1_idx` ON `database_elo`.`Servico` (`Categoria_Especifica_id_categoria_especifica` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Publicacao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Publicacao` (
  `id_publicacao` INT NOT NULL AUTO_INCREMENT,
  `fk_profissional_usuario_id` INT NOT NULL,
  `fk_categoria_Especifica_id` INT NOT NULL,
  `ds_publicacao` VARCHAR(100) NULL,
  `dt_publicacao` DATETIME NULL,
  `st_ativo` TINYINT NULL,
  PRIMARY KEY (`id_publicacao`),
  CONSTRAINT `fk_Publicacao_Profissional1`
    FOREIGN KEY (`fk_profissional_usuario_id`)
    REFERENCES `database_elo`.`Profissional` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Publicacao_Categoria_Especifica1`
    FOREIGN KEY (`fk_categoria_Especifica_id`)
    REFERENCES `database_elo`.`Categoria_Especifica` (`id_categoria_especifica`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Publicacao_Profissional1_idx` ON `database_elo`.`Publicacao` (`fk_profissional_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Publicacao_Categoria_Especifica1_idx` ON `database_elo`.`Publicacao` (`fk_categoria_Especifica_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Publicacao_Imagem`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Publicacao_Imagem` (
  `id_publicacao_Imagem` INT NOT NULL AUTO_INCREMENT,
  `fk_publicacao_id` INT NOT NULL,
  `url_imagem` VARCHAR(500) NULL,
  `nr_ordem` INT NULL,
  PRIMARY KEY (`id_publicacao_Imagem`),
  CONSTRAINT `fk_Publicacao_Imagem_Publicacao1`
    FOREIGN KEY (`fk_publicacao_id`)
    REFERENCES `database_elo`.`Publicacao` (`id_publicacao`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Publicacao_Imagem_Publicacao1_idx` ON `database_elo`.`Publicacao_Imagem` (`fk_publicacao_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Publicacao_Curtida`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Publicacao_Curtida` (
  `id_publicacao` INT NOT NULL AUTO_INCREMENT,
  `id_cliente_usuario` INT NOT NULL,
  `dt_curtida` DATETIME NULL,
  PRIMARY KEY (`id_publicacao`, `id_cliente_usuario`),
  CONSTRAINT `fk_Publicacao_Curtida_Publicacao1`
    FOREIGN KEY (`id_publicacao`)
    REFERENCES `database_elo`.`Publicacao` (`id_publicacao`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Publicacao_Curtida_Cliente1`
    FOREIGN KEY (`id_cliente_usuario`)
    REFERENCES `database_elo`.`Cliente` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Publicacao_Curtida_Cliente1_idx` ON `database_elo`.`Publicacao_Curtida` (`id_cliente_usuario` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Publicacao_Comentario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Publicacao_Comentario` (
  `id_publicacao_comentario` INT NOT NULL AUTO_INCREMENT,
  `fk_publicacao_id` INT NOT NULL,
  `fk_cliente_usuario_id` INT NOT NULL,
  `fk_profissional_usuario_id` INT NOT NULL,
  `fk_comentario_pai` INT NOT NULL,
  `ds_comentario` VARCHAR(200) NULL,
  `dt_comentario` DATETIME NULL,
  `st_ativo` TINYINT NULL,
  PRIMARY KEY (`id_publicacao_comentario`),
  CONSTRAINT `fk_Publicacao_Comentario_Publicacao1`
    FOREIGN KEY (`fk_publicacao_id`)
    REFERENCES `database_elo`.`Publicacao` (`id_publicacao`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Publicacao_Comentario_Cliente1`
    FOREIGN KEY (`fk_cliente_usuario_id`)
    REFERENCES `database_elo`.`Cliente` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Publicacao_Comentario_Profissional1`
    FOREIGN KEY (`fk_profissional_usuario_id`)
    REFERENCES `database_elo`.`Profissional` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Publicacao_Comentario_Publicacao_Comentario1`
    FOREIGN KEY (`fk_comentario_pai`)
    REFERENCES `database_elo`.`Publicacao_Comentario` (`id_publicacao_comentario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Publicacao_Comentario_Cliente1_idx` ON `database_elo`.`Publicacao_Comentario` (`fk_cliente_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Publicacao_Comentario_Profissional1_idx` ON `database_elo`.`Publicacao_Comentario` (`fk_profissional_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Publicacao_Comentario_Publicacao_Comentario_idx` ON `database_elo`.`Publicacao_Comentario` (`fk_comentario_pai` ASC) INVISIBLE;

CREATE INDEX `fk_Publicacao_Comentario_Publicacao_idx` ON `database_elo`.`Publicacao_Comentario` (`fk_publicacao_id` ASC, `dt_comentario` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Reserva_Status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Reserva_Status` (
  `id_reserva_status` INT NOT NULL AUTO_INCREMENT,
  `ds_status` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_reserva_status`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `database_elo`.`Reserva`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Reserva` (
  `id_reserva` INT NOT NULL AUTO_INCREMENT,
  `fk_servico_id` INT NOT NULL,
  `fk_cliente_usuario_id` INT NOT NULL,
  `fk_reserva_Status_id` INT NOT NULL,
  `dt_reserva` DATETIME NULL,
  `ds_descricao` VARCHAR(100) NULL,
  `ds_observacao_profissional` VARCHAR(200) NULL,
  `dt_inicio` DATETIME NULL,
  `dt_fim` DATETIME NULL,
  `ds_endereco` VARCHAR(200) NULL,
  PRIMARY KEY (`id_reserva`),
  CONSTRAINT `fk_Reserva_Servico1`
    FOREIGN KEY (`fk_servico_id`)
    REFERENCES `database_elo`.`Servico` (`id_servico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reserva_Cliente1`
    FOREIGN KEY (`fk_cliente_usuario_id`)
    REFERENCES `database_elo`.`Cliente` (`usuario_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reserva_Reserva_Status1`
    FOREIGN KEY (`fk_reserva_Status_id`)
    REFERENCES `database_elo`.`Reserva_Status` (`id_reserva_status`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Reserva_Servico1_idx` ON `database_elo`.`Reserva` (`fk_servico_id` ASC) VISIBLE;

CREATE INDEX `fk_Reserva_Cliente1_idx` ON `database_elo`.`Reserva` (`fk_cliente_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Reserva_Reserva_Status1_idx` ON `database_elo`.`Reserva` (`fk_reserva_Status_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Servico_Disponibilidade`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Servico_Disponibilidade` (
  `id_servico_disponibilidade` INT NOT NULL AUTO_INCREMENT,
  `fk_servico_id` INT NOT NULL,
  `dia_semana` TINYINT NULL,
  `hr_inicio` TIME NULL,
  `hr_fim` TIME NULL,
  `st_ativo` TINYINT NULL,
  `dt_criacao` DATETIME NULL,
  PRIMARY KEY (`id_servico_disponibilidade`),
  CONSTRAINT `fk_Servico_Disponibilidade_Servico1`
    FOREIGN KEY (`fk_servico_id`)
    REFERENCES `database_elo`.`Servico` (`id_servico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Servico_Disponibilidade_Servico1_idx` ON `database_elo`.`Servico_Disponibilidade` (`fk_servico_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Avaliacao_Reserva`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Avaliacao_Reserva` (
  `idAvaliacao_Reserva` INT NOT NULL AUTO_INCREMENT,
  `fk_reserva_id` INT NOT NULL,
  `fk_avaliador_usuario_id` INT NOT NULL,
  `fk_usuario_avaliado_id` INT NOT NULL,
  `qt_nota` TINYINT NULL,
  `ds_comentario` VARCHAR(200) NULL,
  `observacao_profisional` VARCHAR(200) NULL,
  `distancia` DOUBLE NULL,
  `ds_endereco` VARCHAR(45) NULL,
  PRIMARY KEY (`idAvaliacao_Reserva`),
  CONSTRAINT `fk_Avaliacao_Reserva_Reserva1`
    FOREIGN KEY (`fk_reserva_id`)
    REFERENCES `database_elo`.`Reserva` (`id_reserva`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Avaliacao_Reserva_Usuario1`
    FOREIGN KEY (`fk_avaliador_usuario_id`)
    REFERENCES `database_elo`.`Usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Avaliacao_Reserva_Usuario2`
    FOREIGN KEY (`fk_usuario_avaliado_id`)
    REFERENCES `database_elo`.`Usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Avaliacao_Reserva_Reserva1_idx` ON `database_elo`.`Avaliacao_Reserva` (`fk_reserva_id` ASC) VISIBLE;

CREATE INDEX `fk_Avaliacao_Reserva_Usuario1_idx` ON `database_elo`.`Avaliacao_Reserva` (`fk_avaliador_usuario_id` ASC) VISIBLE;

CREATE INDEX `fk_Avaliacao_Reserva_Usuario2_idx` ON `database_elo`.`Avaliacao_Reserva` (`fk_usuario_avaliado_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Reserva_Custos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Reserva_Custos` (
  `id_reserva_custos` INT NOT NULL AUTO_INCREMENT,
  `fk_reserva_id` INT NOT NULL,
  `ds_descricao` VARCHAR(45) NULL,
  `vl_valor` DOUBLE NULL,
  PRIMARY KEY (`id_reserva_custos`),
  CONSTRAINT `fk_Reserva_Custos_Reserva1`
    FOREIGN KEY (`fk_reserva_id`)
    REFERENCES `database_elo`.`Reserva` (`id_reserva`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Reserva_Custos_Reserva1_idx` ON `database_elo`.`Reserva_Custos` (`fk_reserva_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `database_elo`.`Reserva_endereco`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `database_elo`.`Reserva_endereco` (
  `id_reserva_endereco` INT NOT NULL AUTO_INCREMENT,
  `fk_reserva_id` INT NOT NULL,
  `tp_endereco` ENUM('execucao', 'retirada', 'entrega') NULL,
  `nm_rua` VARCHAR(45) NULL,
  `nm_complemento` VARCHAR(45) NULL,
  `nm_bairro` VARCHAR(45) NULL,
  `nm_cidade` VARCHAR(45) NULL,
  `nm_estado` VARCHAR(45) NULL,
  `nr_cep` VARCHAR(45) NULL,
  `tp_execucao` ENUM('presencial', 'remoto', 'retirada_entrega') NOT NULL,
  PRIMARY KEY (`id_reserva_endereco`),
  CONSTRAINT `fk_Reserva_endereco_Reserva1`
    FOREIGN KEY (`fk_reserva_id`)
    REFERENCES `database_elo`.`Reserva` (`id_reserva`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_Reserva_endereco_Reserva1_idx` ON `database_elo`.`Reserva_endereco` (`fk_reserva_id` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
