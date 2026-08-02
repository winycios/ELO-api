package br.com.elo.eloapi.repository;


import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.orcamento.TipoAutorCancelamento;
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
            "orcamentoStatus",
            "usuarioCancelamento",
            "usuarioConclusao"
    })
    Optional<Orcamento> findByIdAndUsuarioId(Long id, Long usuarioId);

    @EntityGraph(attributePaths = {
            "usuario",
            "servico",
            "servico.profissional",
            "servico.profissional.usuario",
            "servico.categoriaEspecifica",
            "servico.categoriaEspecifica.categoriaGeral",
            "orcamentoStatus",
            "endereco",
            "usuarioCancelamento",
            "usuarioConclusao"
    })
    Optional<Orcamento> findByIdAndServicoProfissionalId(Long id, Long profissionalId);

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
               and orcamento.orcamentoStatus.tipoOrcamentoStatus in :status
               and (:cursorId is null or orcamento.id < :cursorId)
             order by orcamento.id desc
            """)
    List<Orcamento> listarPorProfissional(@Param("profissionalId") Long profissionalId, @Param("status") Collection<TipoOrcamentoStatus> status, @Param("cursorId") Long cursorId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "usuario",
            "servico",
            "servico.profissional",
            "servico.categoriaEspecifica",
            "orcamentoStatus",
            "endereco"
    })
    @Query("""
            select orcamento
             from Orcamento orcamento
             where orcamento.servico.profissional.id = :profissionalId
               and orcamento.orcamentoStatus.tipoOrcamentoStatus in :status
               and coalesce(orcamento.dtInicioProposto, orcamento.dtPreferidoSolicitado) >= :inicioPeriodo
               and coalesce(orcamento.dtInicioProposto, orcamento.dtPreferidoSolicitado) < :fimPeriodo
             order by coalesce(orcamento.dtInicioProposto, orcamento.dtPreferidoSolicitado), orcamento.id
           """)
    List<Orcamento> listarAgendaPorProfissional(
            @Param("profissionalId") Long profissionalId,
            @Param("status") Collection<TipoOrcamentoStatus> status,
            @Param("inicioPeriodo") LocalDateTime inicioPeriodo,
            @Param("fimPeriodo") LocalDateTime fimPeriodo
    );

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

    @Query("""
            select count(orcamento)
              from Orcamento orcamento
             where orcamento.servico.profissional.id = :profissionalId
               and orcamento.id <> :orcamentoId
               and orcamento.orcamentoStatus.tipoOrcamentoStatus in :status
               and orcamento.dtInicioProposto is not null
               and orcamento.dtFimProposto is not null
               and orcamento.dtInicioProposto < :fimComMargem
               and orcamento.dtFimProposto > :inicioSemMargem
            """)
    long contarConflitosAgenda(
            @Param("profissionalId") Long profissionalId,
            @Param("orcamentoId") Long orcamentoId,
            @Param("status") Collection<TipoOrcamentoStatus> status,
            @Param("inicioSemMargem") LocalDateTime inicioSemMargem,
            @Param("fimComMargem") LocalDateTime fimComMargem
    );

    @Query("""
            select coalesce(sum(case
                       when orcamento.orcamentoStatus.tipoOrcamentoStatus = :statusPendente
                       then 1 else 0 end), 0) as orcamentosPendentes,
                   coalesce(sum(case
                       when orcamento.orcamentoStatus.tipoOrcamentoStatus in :statusAgenda
                        and coalesce(orcamento.dtInicioProposto, orcamento.dtPreferidoSolicitado) >= :inicioHoje
                        and coalesce(orcamento.dtInicioProposto, orcamento.dtPreferidoSolicitado) < :fimHoje
                       then 1 else 0 end), 0) as servicosHoje,
                   coalesce(sum(case
                       when orcamento.dtCriacao >= :inicioMes
                        and orcamento.dtCriacao < :fimMes
                       then 1 else 0 end), 0) as solicitacoesMes,
                   coalesce(sum(case
                       when orcamento.dtCriacao >= :inicioMes
                        and orcamento.dtCriacao < :fimMes
                        and (
                            orcamento.orcamentoStatus.tipoOrcamentoStatus in :statusRespondidos
                            or (
                                orcamento.orcamentoStatus.tipoOrcamentoStatus = :statusCancelado
                                and (
                                    orcamento.autorCancelamento = :autorProfissional
                                    or orcamento.dtInicioProposto is not null
                                )
                            )
                        )
                       then 1 else 0 end), 0) as solicitacoesRespondidasMes
              from Orcamento orcamento
             where orcamento.servico.profissional.id = :profissionalId
            """)
    DashboardContadores buscarContadoresDashboard(
            @Param("profissionalId") Long profissionalId,
            @Param("statusPendente") TipoOrcamentoStatus statusPendente,
            @Param("statusAgenda") Collection<TipoOrcamentoStatus> statusAgenda,
            @Param("statusRespondidos") Collection<TipoOrcamentoStatus> statusRespondidos,
            @Param("statusCancelado") TipoOrcamentoStatus statusCancelado,
            @Param("autorProfissional") TipoAutorCancelamento autorProfissional,
            @Param("inicioHoje") LocalDateTime inicioHoje,
            @Param("fimHoje") LocalDateTime fimHoje,
            @Param("inicioMes") LocalDateTime inicioMes,
            @Param("fimMes") LocalDateTime fimMes
    );

    @Query("""
            select orcamento.id as orcamentoId,
                   orcamento.dtConclusao as dataConclusao,
                   orcamento.servico.categoriaEspecifica.id as categoriaEspecificaId,
                   orcamento.servico.categoriaEspecifica.nmCategoria as categoriaEspecifica,
                   coalesce(sum(custo.vl_valor), orcamento.servico.vlServico, 0.0) as valor
              from Orcamento orcamento
              left join OrcamentoCusto custo on custo.orcamento.id = orcamento.id
             where orcamento.servico.profissional.id = :profissionalId
               and orcamento.orcamentoStatus.tipoOrcamentoStatus = :statusConcluido
               and orcamento.dtConclusao >= :inicioPeriodo
               and orcamento.dtConclusao < :fimPeriodo
             group by orcamento.id,
                      orcamento.dtConclusao,
                      orcamento.servico.categoriaEspecifica.id,
                      orcamento.servico.categoriaEspecifica.nmCategoria,
                      orcamento.servico.vlServico
             order by orcamento.dtConclusao
            """)
    List<DashboardServicoConcluido> buscarServicosConcluidosDashboard(
            @Param("profissionalId") Long profissionalId,
            @Param("statusConcluido") TipoOrcamentoStatus statusConcluido,
            @Param("inicioPeriodo") LocalDateTime inicioPeriodo,
            @Param("fimPeriodo") LocalDateTime fimPeriodo
    );

    interface DashboardContadores {
        Long getOrcamentosPendentes();

        Long getServicosHoje();

        Long getSolicitacoesMes();

        Long getSolicitacoesRespondidasMes();
    }

    interface DashboardServicoConcluido {
        Long getOrcamentoId();

        LocalDateTime getDataConclusao();

        Long getCategoriaEspecificaId();

        String getCategoriaEspecifica();

        Double getValor();
    }


    interface IntervaloOcupado {
        LocalDateTime getInicio();

        LocalDateTime getFim();
    }
}
