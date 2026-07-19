package br.com.elo.eloapi.model.avaliacao;

import br.com.elo.eloapi.model.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "avaliacao_reserva")
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

    @Column(name = "qt_nota")
    private Integer nota;

    @Column(name = "ds_comentario", length = 200)
    private String comentario;
}
