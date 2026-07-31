package br.com.elo.eloapi.model.orcamento;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TipoAutorCancelamento {

    USUARIO("usuario"),
    PROFISSIONAL("profissional"),
    SISTEMA("sistema");

    private final String descricao;

    public static TipoAutorCancelamento buscarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("O autor do cancelamento não pode ser vazio.");
        }

        String normalizado = tipo.trim();
        return Arrays.stream(values())
                .filter(autor -> autor.descricao.equalsIgnoreCase(normalizado)
                        || autor.name().equalsIgnoreCase(normalizado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Autor de cancelamento inválido: " + tipo));
    }
}
