package br.com.elo.eloapi.model.servico;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "servico_disponibilidade")
public class ServicoDisponibilidade implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico_disponibilidade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_servico", nullable = false)
    private Servico servico;

    @Column(name = "nr_dia_semana")
    private Integer diaSemana;

    @Column(name = "hr_inicio")
    private LocalTime hrInicio;

    @Column(name = "hr_fim")
    private LocalTime hrFim;

    @Column(name = "st_ativo")
    private Boolean stAtivo;

    @CreationTimestamp
    @Column(updatable = false, name = "dt_criacao")
    private LocalDateTime dtCriacao;

}
