package br.com.elo.eloapi.model.storage;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Getter
public enum EscopoImagem {

    PERFIL("perfil", true),
    SERVICO("servico", true),
    PUBLICACAO("publicacao", true),
    ORCAMENTO("orcamento", false);

    private final String prefixo;
    private final boolean publico;

    EscopoImagem(String prefixo, boolean publico) {
        this.prefixo = prefixo;
        this.publico = publico;
    }

    public static Optional<EscopoImagem> porPrefixo(String prefixo) {
        if (prefixo == null || prefixo.isBlank()) {
            return Optional.empty();
        }

        String normalizado = prefixo.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values()).filter(escopo -> escopo.prefixo.equals(normalizado)).findFirst();
    }

    public static String prefixosAceitos() {
        return Arrays.stream(values()).map(EscopoImagem::getPrefixo).reduce((a, b) -> a + ", " + b).orElse("");
    }
}
