package br.com.elo.eloapi.model.categoria;


import br.com.elo.eloapi.model.profissional.Profissional;
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
@Table(name = "categoria_especifica")
public class CategoriaEspecifica implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_categoria_especifica")
    private Long id;

    @Column(name = "nm_categoria_especifica")
    private String nmCategoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_categoria_geral", nullable = false)
    private CategoriaGeral categoriaGeral;
}
