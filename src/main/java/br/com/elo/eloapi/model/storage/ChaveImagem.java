package br.com.elo.eloapi.model.storage;

import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Pattern;

// Formato da chave do objeto no bucket: {escopo}/{ano}/{mes}/{uuid}.{extensao}.
public final class ChaveImagem {

    private static final Pattern PADRAO = Pattern.compile("^(perfil|servico|publicacao|orcamento)/\\d{4}/\\d{2}/" + "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}" + "\\.(jpg|png|webp)$");

    private ChaveImagem() {
    }

    public static String gerar(EscopoImagem escopo, TipoImagem tipo, LocalDate data, UUID identificador) {
        return String.format(
                "%s/%d/%02d/%s.%s",
                escopo.getPrefixo(),
                data.getYear(),
                data.getMonthValue(),
                identificador,
                tipo.getExtensao()
        );
    }

    public static boolean valida(EscopoImagem escopo, String chave) {
        return chave != null && PADRAO.matcher(chave).matches() && chave.startsWith(escopo.getPrefixo() + "/");
    }
}
