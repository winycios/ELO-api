package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.search.SearchOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchOutboxRepository extends JpaRepository<SearchOutbox, Long> {

    List<SearchOutbox> findTop100ByDtProcessamentoIsNullAndNrTentativasLessThanOrderByIdAsc(Integer maxTentativas);
}
