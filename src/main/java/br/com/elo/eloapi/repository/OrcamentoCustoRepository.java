package br.com.elo.eloapi.repository;


import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.orcamento.OrcamentoCusto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoCustoRepository extends JpaRepository<OrcamentoCusto, Long> {
}
