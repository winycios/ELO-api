package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.orcamento.OrcamentoEndereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrcamentoEnderecoRepository extends JpaRepository<OrcamentoEndereco, Long> {

    Optional<OrcamentoEndereco> findFirstByOrcamentoIdOrderByIdAsc(Long orcamentoId);
}
