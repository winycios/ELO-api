package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.notificacao.NotificacaoOutbox;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacaoOutboxRepository extends JpaRepository<NotificacaoOutbox, Long> {

    @Query(value = """
            select *
              from notificacao_outbox
             where nr_tentativas < :maxTentativas
               and (
                    (tp_status = 'PENDENTE' and dt_proxima_tentativa <= :agora)
                    or
                    (tp_status = 'PROCESSANDO' and dt_processando_desde < :limiteProcessamento)
               )
             order by id_notificacao_outbox
             limit 100
             for update skip locked
            """, nativeQuery = true)
    List<NotificacaoOutbox> reservarPendentes(
            @Param("agora") LocalDateTime agora,
            @Param("limiteProcessamento") LocalDateTime limiteProcessamento,
            @Param("maxTentativas") Integer maxTentativas
    );

    @EntityGraph(attributePaths = {
            "notificacao",
            "notificacao.destinatario",
            "notificacao.orcamento",
            "dispositivo"
    })
    @Query("select outbox from NotificacaoOutbox outbox where outbox.id = :id")
    Optional<NotificacaoOutbox> buscarDetalhadoPorId(@Param("id") Long id);
}
