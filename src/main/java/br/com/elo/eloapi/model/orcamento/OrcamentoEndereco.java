package br.com.elo.eloapi.model.orcamento;

import br.com.elo.eloapi.model.endereco.TipoEndereco;
import br.com.elo.eloapi.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orcamento_endereco")
public class OrcamentoEndereco implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento_endereco")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_orcamento", nullable = false)
    private Orcamento orcamento;

    @Column(name = "nm_rua")
    private String nmRua;

    @Column(name = "nm_complemento")
    private String nmComplemento;

    @Column(name = "nm_bairro")
    private String nmBairro;

    @Column(name = "nm_cidade")
    private String nmCidade;

    @Column(name = "nm_estado")
    private String nmEstado;

    @Column(name = "nr_cep")
    private String nrCep;

    @Column(name = "nr_rua")
    private Integer nrRua;

    @Column(name = "nr_latitude")
    private Double nrLatitude;

    @Column(name = "nr_longitude")
    private Double nrLongitude;
}
