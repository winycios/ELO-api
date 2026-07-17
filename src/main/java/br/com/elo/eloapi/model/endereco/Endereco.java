package br.com.elo.eloapi.model.endereco;

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
@Table(name = "endereco_usuario")
public class Endereco implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco_usuario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nm_apelido")
    private String nmApelido;

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

    @Column(name = "st_tipo")
    private TipoEndereco tipoEndereco;

    @Column(name = "nr_rua")
    private Integer nrRua;

    @CreationTimestamp
    @Column(updatable = false, name = "dt_criacao")
    private LocalDateTime dtCriacao;

    @Column(name = "st_principal")
    private Boolean stPrincipal;

    @Column(name = "nr_latitude")
    private Double nrLatitude;

    @Column(name = "nr_longitude")
    private Double nrLongitude;

    @Column(name = "st_ativo")
    private Boolean stAtivo;
}
