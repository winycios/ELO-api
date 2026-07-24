package br.com.elo.eloapi.model.orcamentoStatus;

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
@Table(name = "orcamento_status")
public class OrcamentoStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_orcamento_status")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = TipoOrcamentoStatusConverter.class)
    @Column(name = "ds_status", nullable = false, unique = true, length = 100)
    private TipoOrcamentoStatus tipoOrcamentoStatus;
}
