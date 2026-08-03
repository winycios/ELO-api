package br.com.elo.eloapi.model.notificacao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "notificacao_outbox",
        uniqueConstraints = @UniqueConstraint(name = "uk_notificacao_outbox_idempotencia", columnNames = "cd_idempotencia")
)
public class NotificacaoOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao_outbox")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_notificacao_id", nullable = false)
    private Notificacao notificacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_usuario_dispositivo_id")
    private DispositivoUsuario dispositivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_canal", nullable = false, length = 20)
    private CanalNotificacao canal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status", nullable = false, length = 20)
    private StatusEnvioNotificacao status = StatusEnvioNotificacao.PENDENTE;

    @Column(name = "cd_idempotencia", nullable = false, length = 200)
    private String chaveIdempotencia;

    @Column(name = "nr_tentativas", nullable = false)
    private Integer tentativas = 0;

    @Column(name = "dt_proxima_tentativa", nullable = false)
    private LocalDateTime dtProximaTentativa = LocalDateTime.now();

    @Column(name = "dt_processando_desde")
    private LocalDateTime dtProcessandoDesde;

    @Column(name = "dt_processamento")
    private LocalDateTime dtProcessamento;

    @Column(name = "cd_mensagem_provedor", length = 255)
    private String codigoMensagemProvedor;

    @Column(name = "ds_ultimo_erro", length = 1000)
    private String ultimoErro;

    @Version
    @Column(name = "nr_versao", nullable = false)
    private Long versao = 0L;

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;
}
