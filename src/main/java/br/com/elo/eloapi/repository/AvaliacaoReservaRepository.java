package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.avaliacao.AvaliacaoReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoReservaRepository extends JpaRepository<AvaliacaoReserva, Long> {

    @EntityGraph(attributePaths = "avaliador")
    List<AvaliacaoReserva> findTop3ByUsuarioAvaliadoIdOrderByIdDesc(Long usuarioAvaliadoId);

    long countByUsuarioAvaliadoId(Long usuarioAvaliadoId);

    long countByUsuarioAvaliadoIdAndNotaGreaterThanEqual(Long usuarioAvaliadoId, Integer notaMinima);
}
