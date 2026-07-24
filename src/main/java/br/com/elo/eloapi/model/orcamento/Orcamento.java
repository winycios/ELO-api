package br.com.elo.eloapi.model.orcamento;

import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orcamento")
public class Orcamento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_servico", nullable = false)
    private Servico servico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_orcamento_status", nullable = false)
    private OrcamentoStatus orcamentoStatus;

    @Column(name = "ds_descricao", length = 100)
    private String dsDescricao;

    @Column(name = "ds_observacao_profissional", length = 200)
    private String dsObservacaoProfissional;

    @Column(name = "dt_preferido_solicitado")
    private LocalDateTime dtPreferidoSolicitado;

    @Column(name = "dt_inicio_proposto")
    private LocalDateTime dtInicioProposto;

    @Column(name = "dt_fim_proposto")
    private LocalDateTime dtFimProposto;
}