package br.com.elo.eloapi.model.publicacao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PublicacaoCurtidaId implements Serializable {
    @Column(name = "id_publicacao")
    private Long publicacaoId;

    @Column(name = "id_usuario")
    private Long usuarioId;
}
