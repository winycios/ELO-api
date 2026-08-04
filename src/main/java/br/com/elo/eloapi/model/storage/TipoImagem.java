package br.com.elo.eloapi.model.storage;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum TipoImagem {

    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp");

    public static final int TAMANHO_CABECALHO = 12;

    private static final byte[] ASSINATURA_JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] ASSINATURA_PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] ASSINATURA_RIFF = {0x52, 0x49, 0x46, 0x46};
    private static final byte[] ASSINATURA_WEBP = {0x57, 0x45, 0x42, 0x50};

    private final String contentType;
    private final String extensao;

    TipoImagem(String contentType, String extensao) {
        this.contentType = contentType;
        this.extensao = extensao;
    }

    public static Optional<TipoImagem> detectar(byte[] cabecalho) {
        if (cabecalho == null) {
            return Optional.empty();
        }
        if (contemAssinatura(cabecalho, ASSINATURA_JPEG, 0)) {
            return Optional.of(JPEG);
        }
        if (contemAssinatura(cabecalho, ASSINATURA_PNG, 0)) {
            return Optional.of(PNG);
        }
        if (contemAssinatura(cabecalho, ASSINATURA_RIFF, 0) && contemAssinatura(cabecalho, ASSINATURA_WEBP, 8)) {
            return Optional.of(WEBP);
        }
        return Optional.empty();
    }

    public static String extensoesAceitas() {
        return "jpg, png, webp";
    }

    private static boolean contemAssinatura(byte[] cabecalho, byte[] assinatura, int deslocamento) {
        if (cabecalho.length < deslocamento + assinatura.length) {
            return false;
        }
        for (int indice = 0; indice < assinatura.length; indice++) {
            if (cabecalho[deslocamento + indice] != assinatura[indice]) {
                return false;
            }
        }
        return true;
    }
}
