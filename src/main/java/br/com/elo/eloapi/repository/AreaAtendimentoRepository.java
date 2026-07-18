package br.com.elo.eloapi.repository;


import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaAtendimentoRepository extends JpaRepository<AreaAtendimento, Long> {

    Optional<AreaAtendimento> findAreaAtendimentoByProfissional_Id(Long profissionalId);
}