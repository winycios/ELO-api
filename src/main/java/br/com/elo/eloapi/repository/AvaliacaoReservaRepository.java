package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.avaliacao.AvaliacaoReserva;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AvaliacaoReservaRepository extends JpaRepository<AvaliacaoReserva, Long> {

    @EntityGraph(attributePaths = "avaliador")
    @Query("""
            select avaliacao
              from AvaliacaoReserva avaliacao
             where avaliacao.usuarioAvaliado.id = :usuarioAvaliadoId
               and exists (
                    select 1
                      from Orcamento orcamento
                     where orcamento.id = avaliacao.reservaId
                       and orcamento.servico.categoriaEspecifica.categoriaGeral.id = :categoriaGeralId
               )
             order by avaliacao.id desc
            """)
    List<AvaliacaoReserva> findByUsuarioAvaliadoIdAndCategoriaGeralIdOrderByIdDesc(
            @Param("usuarioAvaliadoId") Long usuarioAvaliadoId,
            @Param("categoriaGeralId") Long categoriaGeralId,
            Pageable pageable
    );

    @Query("""
            select count(avaliacao)
              from AvaliacaoReserva avaliacao
             where avaliacao.usuarioAvaliado.id = :usuarioAvaliadoId
               and exists (
                    select 1
                      from Orcamento orcamento
                     where orcamento.id = avaliacao.reservaId
                       and orcamento.servico.categoriaEspecifica.categoriaGeral.id = :categoriaGeralId
               )
            """)
    long countByUsuarioAvaliadoIdAndCategoriaGeralId(
            @Param("usuarioAvaliadoId") Long usuarioAvaliadoId,
            @Param("categoriaGeralId") Long categoriaGeralId
    );

    @Query("""
            select count(avaliacao)
              from AvaliacaoReserva avaliacao
             where avaliacao.usuarioAvaliado.id = :usuarioAvaliadoId
               and avaliacao.nota >= :notaMinima
               and exists (
                    select 1
                      from Orcamento orcamento
                     where orcamento.id = avaliacao.reservaId
                       and orcamento.servico.categoriaEspecifica.categoriaGeral.id = :categoriaGeralId
               )
            """)
    long countByUsuarioAvaliadoIdAndCategoriaGeralIdAndNotaGreaterThanEqual(
            @Param("usuarioAvaliadoId") Long usuarioAvaliadoId,
            @Param("categoriaGeralId") Long categoriaGeralId,
            @Param("notaMinima") Integer notaMinima
    );

    boolean existsByReservaIdAndAvaliadorId(Long reservaId, Long avaliadorId);

    @Query("""
            select avaliacao.reservaId
              from AvaliacaoReserva avaliacao
             where avaliacao.reservaId in :orcamentoIds
               and avaliacao.avaliador.id = :avaliadorId
            """)
    List<Long> findOrcamentoIdsAvaliados(
            @Param("orcamentoIds") Collection<Long> orcamentoIds,
            @Param("avaliadorId") Long avaliadorId
    );
}
