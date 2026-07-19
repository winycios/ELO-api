package br.com.elo.eloapi.model.search;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "search_outbox")
public class SearchOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_search_outbox")
    private Long id;

    @Column(name = "fk_profissional_id", nullable = false)
    private Long profissionalId;

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "dt_processamento")
    private LocalDateTime dtProcessamento;

    @Column(name = "nr_tentativas", nullable = false)
    private Integer nrTentativas = 0;

    public SearchOutbox(Long profissionalId) {
        this.profissionalId = profissionalId;
    }
}
