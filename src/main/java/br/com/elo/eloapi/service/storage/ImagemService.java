package br.com.elo.eloapi.service.storage;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.model.imagem.dto.ImagemUploadRS;
import br.com.elo.eloapi.implementation.ArmazenamentoArquivo;
import br.com.elo.eloapi.model.storage.ArquivoArmazenado;
import br.com.elo.eloapi.model.storage.ChaveImagem;
import br.com.elo.eloapi.model.storage.EscopoImagem;
import br.com.elo.eloapi.model.storage.TipoImagem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

@Service
public class ImagemService {

    private static final Logger logger = LoggerFactory.getLogger(ImagemService.class);

    private final ArmazenamentoArquivo armazenamento;
    private final long tamanhoMaximoBytes;

    public ImagemService(ArmazenamentoArquivo armazenamento, @Value("${elo.storage.tamanho-maximo-bytes:5242880}") long tamanhoMaximoBytes) {
        this.armazenamento = armazenamento;
        this.tamanhoMaximoBytes = tamanhoMaximoBytes;
    }

    public ImagemUploadRS enviar(String escopoInformado, MultipartFile arquivo) {
        EscopoImagem escopo = EscopoImagem.porPrefixo(escopoInformado).orElseThrow(() -> new BadRequestException("Escopo inválido. Valores aceitos: " + EscopoImagem.prefixosAceitos() + "."));

        byte[] conteudo = lerConteudo(arquivo);
        TipoImagem tipo = detectarTipo(conteudo);

        ArquivoArmazenado armazenado = armazenamento.armazenar(escopo, conteudo, tipo);
        logger.info("Imagem armazenada no escopo {} com chave {} ({} bytes)", escopo.getPrefixo(), armazenado.chave(), armazenado.tamanhoBytes());

        return new ImagemUploadRS(armazenado.chave(), armazenado.url());
    }


    public void validarChaves(EscopoImagem escopo, List<String> chaves) {
        if (chaves == null || chaves.isEmpty()) {
            return;
        }

        for (String chave : chaves) {
            if (!ChaveImagem.valida(escopo, chave)) {
                throw new BadRequestException("Chave de imagem inválida: " + chave + ". Envie a imagem em POST /imagem/" + escopo.getPrefixo() + " e use a chave retornada.");
            }
            if (!armazenamento.existe(escopo, chave)) {
                throw new BadRequestException("A imagem " + chave + " não foi encontrada no storage.");
            }
        }
    }

    public void validarChave(EscopoImagem escopo, String chave) {
        validarChaves(escopo, List.of(chave));
    }

    public String urlLeitura(EscopoImagem escopo, String chave) {
        return armazenamento.gerarUrlLeitura(escopo, chave);
    }

    public UnaryOperator<String> resolvedorDeUrl(EscopoImagem escopo) {
        return chave -> armazenamento.gerarUrlLeitura(escopo, chave);
    }

    public void remover(EscopoImagem escopo, String chave) {
        armazenamento.remover(escopo, chave);
    }

    private byte[] lerConteudo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BadRequestException("O arquivo de imagem é obrigatório.");
        }
        if (arquivo.getSize() > tamanhoMaximoBytes) {
            throw new BadRequestException("A imagem excede o tamanho máximo de " + (tamanhoMaximoBytes / 1024 / 1024) + " MB.");
        }

        try {
            return arquivo.getBytes();
        } catch (IOException excecao) {
            logger.error("Falha ao ler o arquivo enviado", excecao);
            throw new BadRequestException("Não foi possível ler o arquivo enviado.");
        }
    }

    private TipoImagem detectarTipo(byte[] conteudo) {
        byte[] cabecalho = Arrays.copyOf(conteudo, Math.min(conteudo.length, TipoImagem.TAMANHO_CABECALHO));
        return TipoImagem.detectar(cabecalho).orElseThrow(() -> new BadRequestException("Formato de imagem não suportado. Aceitos: " + TipoImagem.extensoesAceitas() + "."));
    }
}
