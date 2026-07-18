package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.publicacao.Publicacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PublicacaoRepository extends JpaRepository<Publicacao, Long> {
    @EntityGraph(attributePaths = {"profissional", "profissional.usuario", "categoriaEspecifica"})
    @Query("""
        select p from Publicacao p
         where p.stAtivo = true
           and (:categoriaId is null or p.categoriaEspecifica.id = :categoriaId)
           and (:isProfissional = false or p.profissional.usuario.id = :idUsuario)
           and (:cursorData is null
                or p.dtPublicacao < :cursorData
                or (p.dtPublicacao = :cursorData and p.id < :cursorId))
         order by p.dtPublicacao desc, p.id desc
        """)
    List<Publicacao> buscarFeed(
            @Param("categoriaId") Long categoriaId,
            @Param("idUsuario") Long idUsuario,
            @Param("isProfissional") Boolean isProfissional,
            @Param("cursorData") LocalDateTime cursorData,
            @Param("cursorId") Long cursorId,
            Pageable pageable);

    Optional<Publicacao> findByIdAndProfissionalIdAndStAtivoTrue(Long id, Long profissionalId);
}
