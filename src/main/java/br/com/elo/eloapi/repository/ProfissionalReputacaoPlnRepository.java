package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.reputacao.ProfissionalReputacaoPln;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfissionalReputacaoPlnRepository extends JpaRepository<ProfissionalReputacaoPln, Long> {

    List<ProfissionalReputacaoPln> findAllByProfissionalIdIn(Collection<Long> profissionalIds);

    Optional<ProfissionalReputacaoPln> findByProfissionalId(Long profissionalId);
}
