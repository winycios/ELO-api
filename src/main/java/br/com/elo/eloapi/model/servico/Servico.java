package br.com.elo.eloapi.model.servico;


import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import br.com.elo.eloapi.model.profissional.Profissional;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "servico")
public class Servico implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_profissional_usuario", nullable = false)
    private Profissional profissional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_categoria_especifica", nullable = false)
    private CategoriaEspecifica categoriaEspecifica;

    @Column(name = "ds_descricao")
    private String dsDescricao;

    @Column(name = "nr_tempo_experiencia")
    private Integer tempoExperiencia;

    @Column(name = "vl_servico")
    private Double vlServico;

    @Column(name = "ds_tag")
    private String dsTag;

    @Column(name = "tp_execucao")
    private TipoServico tipoServico;

    @Column(name = "st_ativo", nullable = false)
    private Boolean stAtivo;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao")
    private LocalDateTime dtAtualizacao;

}
