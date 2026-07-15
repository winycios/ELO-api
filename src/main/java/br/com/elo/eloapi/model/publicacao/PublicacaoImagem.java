package br.com.elo.eloapi.model.publicacao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "publicacao_imagem")
public class PublicacaoImagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacao_Imagem")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_publicacao_id", nullable = false)
    private Publicacao publicacao;

    @Column(name = "url_imagem", length = 500)
    private String url;

    @Column(name = "nr_ordem")
    private Integer ordem;
}
