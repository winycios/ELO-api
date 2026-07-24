package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.orcamento.OrcamentoImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoImagemRepository extends JpaRepository<OrcamentoImagem, Long> {
}
