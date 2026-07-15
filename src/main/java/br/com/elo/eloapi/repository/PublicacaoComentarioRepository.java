package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.publicacao.PublicacaoComentario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PublicacaoComentarioRepository extends JpaRepository<PublicacaoComentario, Long> {
    interface Contagem {
        Long getPublicacaoId();
        Long getTotal();
    }

    @Query("""
        select c.publicacao.id as publicacaoId, count(c) as total
          from PublicacaoComentario c
         where c.publicacao.id in :ids and c.ativo = true
         group by c.publicacao.id
        """)
    List<Contagem> contarAtivosPorPublicacoes(@Param("ids") List<Long> publicacaoIds);

    @EntityGraph(attributePaths = {"usuario", "comentarioPai"})
    @Query("""
        select c from PublicacaoComentario c
         where c.publicacao.id = :publicacaoId
           and c.ativo = true
           and (:cursorData is null
                or c.dataComentario < :cursorData
                or (c.dataComentario = :cursorData and c.id < :cursorId))
         order by c.dataComentario desc, c.id desc
        """)
    List<PublicacaoComentario> buscarPagina(
            @Param("publicacaoId") Long publicacaoId,
            @Param("cursorData") LocalDateTime cursorData,
            @Param("cursorId") Long cursorId,
            Pageable pageable);
}
