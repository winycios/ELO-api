package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.servico.Servico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    @EntityGraph(attributePaths = {"categoriaEspecifica", "categoriaEspecifica.categoriaGeral"})
    List<Servico> findAllByProfissionalIdAndStAtivoTrue(Long profissionalId);

    Optional<Servico> findByIdAndProfissionalIdAndStAtivoTrue(Long id, Long profissionalId);
}
