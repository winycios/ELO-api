package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.notificacao.Notificacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Optional<Notificacao> findByChaveEvento(String chaveEvento);

    @EntityGraph(attributePaths = "orcamento")
    @Query("""
            select notificacao
              from Notificacao notificacao
             where notificacao.destinatario.id = :usuarioId
               and (:cursorId is null or notificacao.id < :cursorId)
             order by notificacao.id desc
            """)
    List<Notificacao> listarPorDestinatario(
            @Param("usuarioId") Long usuarioId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    Optional<Notificacao> findByIdAndDestinatarioId(Long id, Long destinatarioId);

    long countByDestinatarioIdAndDtLeituraIsNull(Long destinatarioId);

    @Modifying
    @Query("""
            update Notificacao notificacao
               set notificacao.dtLeitura = :agora
             where notificacao.destinatario.id = :usuarioId
               and notificacao.dtLeitura is null
            """)
    int marcarTodasComoLidas(@Param("usuarioId") Long usuarioId, @Param("agora") LocalDateTime agora);
}
