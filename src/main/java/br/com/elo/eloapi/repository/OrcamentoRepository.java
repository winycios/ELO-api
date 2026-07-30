package br.com.elo.eloapi.repository;


import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.orcamentoStatus.TipoOrcamentoStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    @EntityGraph(attributePaths = {
            "servico",
            "servico.profissional",
            "servico.profissional.usuario",
            "servico.categoriaEspecifica",
            "servico.categoriaEspecifica.categoriaGeral",
            "orcamentoStatus"
    })
    Optional<Orcamento> findByIdAndUsuarioId(Long id, Long usuarioId);

    @EntityGraph(attributePaths = {
            "servico",
            "servico.profissional",
            "servico.profissional.usuario",
            "servico.categoriaEspecifica",
            "orcamentoStatus"
    })
    @Query("""
            select orcamento
             from Orcamento orcamento
             where orcamento.usuario.id = :usuarioId
               and (:status is null or orcamento.orcamentoStatus.tipoOrcamentoStatus = :status)
               and (:cursorId is null or orcamento.id < :cursorId)
             order by orcamento.id desc
            """)
    List<Orcamento> listarPorCliente(
            @Param("usuarioId") Long usuarioId,
            @Param("status") TipoOrcamentoStatus status,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "servico",
            "servico.profissional",
            "servico.profissional.usuario",
            "servico.categoriaEspecifica",
            "orcamentoStatus",
            "endereco"
    })
    @Query("""
            select orcamento
             from Orcamento orcamento
             where orcamento.servico.profissional.id = :profissionalId
               and (:status is null or orcamento.orcamentoStatus.tipoOrcamentoStatus = :status)
               and (:cursorId is null or orcamento.id < :cursorId)
             order by orcamento.id desc
            """)
    List<Orcamento> listarPorProfissional(@Param("profissionalId") Long profissionalId, @Param("status") TipoOrcamentoStatus status, @Param("cursorId") Long cursorId, Pageable pageable);

    @Query("""
            select orcamento.dtInicioProposto as inicio,
                   orcamento.dtFimProposto as fim
              from Orcamento orcamento
             where orcamento.servico.profissional.id = :profissionalId
               and orcamento.orcamentoStatus.tipoOrcamentoStatus in :status
               and orcamento.dtInicioProposto is not null
               and orcamento.dtFimProposto is not null
               and orcamento.dtInicioProposto < :fimPeriodo
               and orcamento.dtFimProposto > :inicioPeriodo
             order by orcamento.dtInicioProposto
            """)
    List<IntervaloOcupado> buscarIntervalosOcupados(
            @Param("profissionalId") Long profissionalId,
            @Param("status") Collection<TipoOrcamentoStatus> status,
            @Param("inicioPeriodo") LocalDateTime inicioPeriodo,
            @Param("fimPeriodo") LocalDateTime fimPeriodo
    );

    interface IntervaloOcupado {
        LocalDateTime getInicio();

        LocalDateTime getFim();
    }
}
