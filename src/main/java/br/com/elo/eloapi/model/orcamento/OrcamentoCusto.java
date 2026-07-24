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
@Table(name = "orcamento_custos")
public class OrcamentoCusto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento_custos")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_orcamento", nullable = false)
    private Orcamento orcamento;

    @Column(name = "ds_descricao")
    private String dsDescricao;

    @Column(name = "vl_valor")
    private Double vl_valor;
}
