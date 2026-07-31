package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.profissional.Profissional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Optional<Profissional> findByUsuarioEmail(String email);

    @EntityGraph(attributePaths = {"usuario"})
    Optional<Profissional> findByIdAndStHabilitadoTrue(Long id);

    @EntityGraph(attributePaths = {"usuario"})
    List<Profissional> findAllByIdIn(Collection<Long> ids);

    @Query("select p.id from Profissional p order by p.id")
    List<Long> findAllIds();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Profissional p where p.id = :id")
    Optional<Profissional> findByIdForUpdate(@Param("id") Long id);
}
