package br.com.elo.eloapi.implementation;

import br.com.elo.eloapi.model.storage.ArquivoArmazenado;
import br.com.elo.eloapi.model.storage.EscopoImagem;
import br.com.elo.eloapi.model.storage.TipoImagem;

public interface ArmazenamentoArquivo {

    ArquivoArmazenado armazenar(EscopoImagem escopo, byte[] conteudo, TipoImagem tipo);

    String gerarUrlLeitura(EscopoImagem escopo, String chave);

    boolean existe(EscopoImagem escopo, String chave);

    void remover(EscopoImagem escopo, String chave);
}
