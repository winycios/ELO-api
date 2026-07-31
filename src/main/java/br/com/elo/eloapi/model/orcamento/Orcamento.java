package br.com.elo.eloapi.model.orcamento;

import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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

    @OneToOne(mappedBy = "orcamento", fetch = FetchType.LAZY)
    private OrcamentoEndereco endereco;

    @Column(name = "ds_descricao", nullable = false, length = 100)
    private String dsDescricao;

    @Column(name = "ds_observacao_profissional", length = 200)
    private String dsObservacaoProfissional;

    @Column(name = "tp_motivo_cancelamento", length = 50)
    private String motivoCancelamento;

    @Column(name = "ds_descricao_cancelamento", length = 200)
    private String dsDescricaoCancelamento;

    @Convert(converter = TipoAutorCancelamentoConverter.class)
    @Column(name = "tp_autor_cancelamento", length = 20)
    private TipoAutorCancelamento autorCancelamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_usuario_cancelamento")
    private Usuario usuarioCancelamento;

    @Column(name = "dt_cancelamento")
    private LocalDateTime dtCancelamento;

    @Column(name = "dt_preferido_solicitado", nullable = false)
    private LocalDateTime dtPreferidoSolicitado;

    @Column(name = "dt_inicio_proposto")
    private LocalDateTime dtInicioProposto;

    @Column(name = "dt_fim_proposto")
    private LocalDateTime dtFimProposto;

    @CreationTimestamp
    @Column(updatable = false, name = "dt_criacao")
    private LocalDateTime dtCriacao;}
