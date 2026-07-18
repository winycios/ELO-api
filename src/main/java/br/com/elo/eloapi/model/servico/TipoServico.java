package br.com.elo.eloapi.model.servico;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TipoServico {

    PRESENCIAL("presencial"),
    REMOTO("remoto");

    private final String tipoServico;

    public static TipoServico buscarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("O tipo de servico não pode ser vazio");
        }

        String tipoNormalizado = tipo.trim();
        return Arrays.stream(values()).filter(tipoServico -> tipoServico.tipoServico.equalsIgnoreCase(tipoNormalizado) || tipoServico.name().equalsIgnoreCase(tipoNormalizado)).findFirst().orElse(TipoServico.PRESENCIAL);
    }
}
