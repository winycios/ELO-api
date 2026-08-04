package br.com.elo.eloapi.model.orcamento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orcamento_imagem")
public class OrcamentoImagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento_imagem")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_orcamento", nullable = false)
    private Orcamento orcamento;

    /** Chave do objeto no bucket privado. A URL assinada é gerada na leitura. */
    @Column(name = "ds_chave_imagem", nullable = false, length = 500)
    private String chave;
}
