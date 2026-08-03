package br.com.elo.eloapi.model.notificacao;

import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.usuario.Usuario;
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
@Table(name = "notificacao", uniqueConstraints = @UniqueConstraint(name = "uk_notificacao_chave_evento", columnNames = "cd_chave_evento"))
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_usuario_destinatario_id", nullable = false)
    private Usuario destinatario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_orcamento_id", nullable = false)
    private Orcamento orcamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_notificacao", nullable = false, length = 60)
    private TipoNotificacao tipo;

    @Column(name = "cd_chave_evento", nullable = false, length = 160)
    private String chaveEvento;

    @Column(name = "ds_titulo", nullable = false, length = 120)
    private String titulo;

    @Column(name = "ds_mensagem", nullable = false, length = 500)
    private String mensagem;

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "dt_leitura")
    private LocalDateTime dtLeitura;
}
