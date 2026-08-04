package br.com.elo.eloapi.model.imagem.dto;

/**
 * @param chave identificador do objeto no bucket. É este valor que deve ser
 *              enviado nos endpoints de orçamento, serviço e publicação.
 * @param url   URL para exibir a imagem imediatamente após o upload. Não deve
 *              ser persistida pelo app: em escopo privado ela expira.
 */
public record ImagemUploadRS(String chave, String url) {
}
