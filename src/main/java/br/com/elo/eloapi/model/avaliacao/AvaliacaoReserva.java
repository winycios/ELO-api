package br.com.elo.eloapi.model.avaliacao;

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
@Table(name = "avaliacao_reserva", uniqueConstraints = @UniqueConstraint(name = "uk_avaliacao_reserva_avaliador", columnNames = {"fk_id_reserva", "fk_id_avaliador_usuario"}))
public class AvaliacaoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao_reserva")
    private Long id;

    @Column(name = "fk_id_reserva", nullable = false)
    private Long reservaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_avaliador_usuario", nullable = false)
    private Usuario avaliador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_usuario_avaliado", nullable = false)
    private Usuario usuarioAvaliado;

    @Column(name = "qt_nota", nullable = false)
    private Integer nota;

    @Column(name = "ds_comentario", length = 200)
    private String comentario;

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;
}
