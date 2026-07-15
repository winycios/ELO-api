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
@Table(name = "publicacao_curtida")
public class PublicacaoCurtida {
    @EmbeddedId
    private PublicacaoCurtidaId id;

    @MapsId("publicacaoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_publicacao", nullable = false)
    private Publicacao publicacao;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "dt_curtida", updatable = false)
    private LocalDateTime dataCurtida;

    public PublicacaoCurtida(Publicacao publicacao, Usuario usuario) {
        this.id = new PublicacaoCurtidaId(publicacao.getId(), usuario.getId());
        this.publicacao = publicacao;
        this.usuario = usuario;
    }
}
