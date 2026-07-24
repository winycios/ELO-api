package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import br.com.elo.eloapi.model.orcamentoStatus.TipoOrcamentoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrcamentoStatusRepository extends JpaRepository<OrcamentoStatus, Long> {

    Optional<OrcamentoStatus> findByTipoOrcamentoStatus(TipoOrcamentoStatus tipoOrcamentoStatus);
}
