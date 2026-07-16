package br.com.elo.eloapi.model.categoria;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "categoria_geral")
public class CategoriaGeral implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_categoria_geral")
    private Long id;

    @Column(name = "nm_categoria")
    private String nmCategoria;

    @Column(name = "ds_icon")
    private String dsIcon;

}
