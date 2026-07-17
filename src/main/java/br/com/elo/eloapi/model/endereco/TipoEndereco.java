package br.com.elo.eloapi.model.endereco;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TipoEndereco {

    CASA("casa"),
    EMPRESA("empresa"),
    COMERCIAL("comercial");

    private final String tipoEndereco;

    public static TipoEndereco buscarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("O tipo de endereço não pode ser vazio");
        }

        String tipoNormalizado = tipo.trim();
        return Arrays.stream(values()).filter(tipoEndereco -> tipoEndereco.tipoEndereco.equalsIgnoreCase(tipoNormalizado) || tipoEndereco.name().equalsIgnoreCase(tipoNormalizado)).findFirst().orElse(TipoEndereco.CASA);
    }
}
