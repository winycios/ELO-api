package br.com.elo.eloapi.model.publicacao;

import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import br.com.elo.eloapi.model.profissional.Profissional;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "publicacao")
public class Publicacao implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_profissional_usuario_id", nullable = false)
    private Profissional profissional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_categoria_Especifica_id", nullable = false)
    private CategoriaEspecifica categoriaEspecifica;

    @Column(name = "ds_publicacao")
    private String dsPublicacao;

    @Column(name = "dt_publicacao")
    private LocalDateTime dtPublicacao;

    @Column(name = "st_ativo")
    private Boolean stAtivo;
}
