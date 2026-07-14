package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.profissional.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Optional<Profissional> findByUsuarioEmail(String email);

}
