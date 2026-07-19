package br.com.elo.eloapi.model.profissional;

import br.com.elo.eloapi.model.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Profissional")
public class Profissional implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "usuario_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "dt_criacao", updatable = false)
    private LocalDateTime dtCriacao;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao")
    private LocalDateTime dtAtualizacao;

    @Column(name = "qt_resposta_geral")
    private Integer qtRespostaGeral;

    @Column(name = "qt_servicos_concluido")
    private Integer qtServicoConcluido;

    @Column(name = "st_disponivel")
    private Boolean stDisponivel;

    @Column(name = "ds_apresentacao")
    private String dsApresentacao;

    @Column(name = "uri_perfil")
    private String uriPerfil;

    @Column(name = "ds_especialidades")
    private String dsEspecialidades;

    @Column(name = "st_habilitado")
    @NotNull
    private Boolean stHabilitado;

    public Profissional(Usuario usuario, Boolean stHabilitado) {
        this.usuario = usuario;
        this.stHabilitado = stHabilitado;
    }
}
