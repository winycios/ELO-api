package br.com.elo.eloapi.model.reputacao;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Immutable
@Entity
@Table(name = "profissional_reputacao_pln")
public class ProfissionalReputacaoPln implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_profissional_reputacao_pln")
    private Long id;

    @Column(name = "fk_id_profissional", nullable = false)
    private Long profissionalId;

    @Column(name = "qt_comentarios_processados")
    private Integer comentariosProcessados;

    @Column(name = "qt_positivo")
    private Integer quantidadePositivo;

    @Column(name = "qt_neutro")
    private Integer quantidadeNeutro;

    @Column(name = "qt_negativo")
    private Integer quantidadeNegativo;

    @Column(name = "nr_percentual_positivo")
    private Double percentualPositivo;

    @Column(name = "nr_percentual_neutro")
    private Double percentualNeutro;

    @Column(name = "nr_percentual_negativo")
    private Double percentualNegativo;

    /** Media do sentimento normalizada entre 0.0 (negativo) e 1.0 (positivo). */
    @Column(name = "nr_sentimento_medio")
    private Double sentimentoMedio;

    @Column(name = "qt_inconsistencias")
    private Integer quantidadeInconsistencias;

    @Column(name = "nr_taxa_inconsistencia")
    private Double taxaInconsistencia;

    @Convert(converter = ListaTextoJsonConverter.class)
    @Column(name = "js_pontos_fortes")
    private List<String> pontosFortes;

    @Convert(converter = ListaTextoJsonConverter.class)
    @Column(name = "js_pontos_fracos")
    private List<String> pontosFracos;

    @Column(name = "ds_resumo")
    private String resumo;

    @Column(name = "cd_versao_modelo", nullable = false)
    private String versaoModelo;

    @Column(name = "dt_atualizacao", nullable = false)
    private LocalDateTime dtAtualizacao;
}
