package br.com.elo.eloapi.model.areaAtendimento;

import br.com.elo.eloapi.model.profissional.Profissional;
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
@Table(name = "area_atendimento")
public class AreaAtendimento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_area_atendimento")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_profissional", nullable = false)
    private Profissional profissional;

    @Column(name = "nr_latitude")
    private Double nrLatitude;

    @Column(name = "nr_longitude")
    private Double nrLongitude;

    @Column(name = "nr_raio")
    private Integer nrRaio;

    @Column(name = "nm_cidade")
    private String nmCidade;

    @Column(name = "nm_estado")
    private String nmEstado;

    @Column(name = "nm_bairro")
    private String nmBairro;

    @CreationTimestamp
    @Column(name = "dt_criacao", updatable = false)
    private LocalDateTime dtCriacao;
}
