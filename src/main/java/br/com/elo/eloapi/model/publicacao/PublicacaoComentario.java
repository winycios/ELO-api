package br.com.elo.eloapi.model.publicacao;

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
@Table(name = "publicacao_comentario")
public class PublicacaoComentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacao_comentario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_publicacao_id", nullable = false)
    private Publicacao publicacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_comentario_pai")
    private PublicacaoComentario comentarioPai;

    @Column(name = "ds_comentario", length = 200)
    private String texto;

    @CreationTimestamp
    @Column(name = "dt_comentario", updatable = false)
    private LocalDateTime dataComentario;

    @Column(name = "st_ativo")
    private Boolean ativo;

    public PublicacaoComentario(Publicacao publicacao, Usuario usuario,
                                PublicacaoComentario comentarioPai, String texto) {
        this.publicacao = publicacao;
        this.usuario = usuario;
        this.comentarioPai = comentarioPai;
        this.texto = texto;
        this.ativo = true;
    }
}
