package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.orcamento.OrcamentoImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrcamentoImagemRepository extends JpaRepository<OrcamentoImagem, Long> {

    List<OrcamentoImagem> findAllByOrcamentoIdOrderByIdAsc(Long orcamentoId);
}
