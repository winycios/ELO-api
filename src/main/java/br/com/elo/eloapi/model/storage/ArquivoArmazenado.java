package br.com.elo.eloapi.model.storage;

public record ArquivoArmazenado(String chave, String url, long tamanhoBytes, String contentType) {
}
