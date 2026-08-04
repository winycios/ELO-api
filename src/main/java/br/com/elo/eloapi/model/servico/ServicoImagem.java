package br.com.elo.eloapi.model.servico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "servico_imagem")
public class ServicoImagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico_Imagem")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_servico", nullable = false)
    private Servico servico;

    /** Chave do objeto no bucket público. A URL é resolvida na leitura. */
    @Column(name = "ds_chave_imagem", length = 500)
    private String chave;

    @Column(name = "nr_ordem")
    private Integer ordem;
}
