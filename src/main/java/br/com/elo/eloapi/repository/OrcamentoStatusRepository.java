package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoStatusRepository extends JpaRepository<OrcamentoStatus, Long> {}
