package br.com.elo.eloapi.model.notificacao;

import br.com.elo.eloapi.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario_dispositivo", uniqueConstraints = {@UniqueConstraint(name = "uk_usuario_dispositivo_codigo", columnNames = {"fk_usuario_id", "cd_dispositivo"}), @UniqueConstraint(name = "uk_usuario_dispositivo_fcm", columnNames = "ds_identificador_fcm")})
public class DispositivoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_dispositivo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "cd_dispositivo", nullable = false, length = 100)
    private String codigoDispositivo;

    @Column(name = "ds_identificador_fcm", nullable = false, length = 512)
    private String identificadorFcm;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_identificador_fcm", nullable = false, length = 10)
    private TipoIdentificadorFcm tipoIdentificador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_plataforma", nullable = false, length = 10)
    private PlataformaDispositivo plataforma;

    @Column(name = "st_ativo", nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao", nullable = false)
    private LocalDateTime dtAtualizacao;
}
