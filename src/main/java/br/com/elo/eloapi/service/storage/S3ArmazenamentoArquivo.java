package br.com.elo.eloapi.service.storage;

import br.com.elo.eloapi.implementation.ArmazenamentoArquivo;
import br.com.elo.eloapi.model.storage.ArquivoArmazenado;
import br.com.elo.eloapi.model.storage.ChaveImagem;
import br.com.elo.eloapi.model.storage.EscopoImagem;
import br.com.elo.eloapi.model.storage.TipoImagem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class S3ArmazenamentoArquivo implements ArmazenamentoArquivo {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketPublico;
    private final String bucketPrivado;
    private final String urlPublicaBase;
    private final Duration expiracaoUrlAssinada;

    public S3ArmazenamentoArquivo(S3Client s3Client, S3Presigner s3Presigner, @Value("${elo.storage.bucket-publico}") String bucketPublico, @Value("${elo.storage.bucket-privado}") String bucketPrivado, @Value("${elo.storage.url-publica-base}") String urlPublicaBase, @Value("${elo.storage.presigned-expiration-seconds:3600}") long expiracaoSegundos) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketPublico = bucketPublico;
        this.bucketPrivado = bucketPrivado;
        this.urlPublicaBase = removerBarraFinal(urlPublicaBase);
        this.expiracaoUrlAssinada = Duration.ofSeconds(expiracaoSegundos);
    }

    @Override
    public ArquivoArmazenado armazenar(EscopoImagem escopo, byte[] conteudo, TipoImagem tipo) {
        String chave = ChaveImagem.gerar(escopo, tipo, LocalDate.now(), UUID.randomUUID());

        PutObjectRequest requisicao = PutObjectRequest.builder().bucket(bucket(escopo)).key(chave).contentType(tipo.getContentType()).contentLength((long) conteudo.length).build();

        s3Client.putObject(requisicao, RequestBody.fromBytes(conteudo));

        return new ArquivoArmazenado(chave, gerarUrlLeitura(escopo, chave), conteudo.length, tipo.getContentType());
    }

    @Override
    public String gerarUrlLeitura(EscopoImagem escopo, String chave) {
        if (chave == null || chave.isBlank()) {
            return null;
        }

        if (chave.startsWith("http://") || chave.startsWith("https://")) {
            return chave;
        }
        if (escopo.isPublico()) {
            return String.format("%s/%s/%s", urlPublicaBase, bucketPublico, chave);
        }
        return assinarLeitura(chave);
    }

    @Override
    public boolean existe(EscopoImagem escopo, String chave) {
        if (chave == null || chave.isBlank()) {
            return false;
        }

        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket(escopo)).key(chave).build());
            return true;
        } catch (NoSuchKeyException excecao) {
            return false;
        }
    }

    @Override
    public void remover(EscopoImagem escopo, String chave) {
        if (chave == null || chave.isBlank()) {
            return;
        }
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket(escopo)).key(chave).build());
    }

    private String assinarLeitura(String chave) {
        GetObjectRequest leitura = GetObjectRequest.builder().bucket(bucketPrivado).key(chave).build();

        GetObjectPresignRequest assinatura = GetObjectPresignRequest.builder().signatureDuration(expiracaoUrlAssinada).getObjectRequest(leitura).build();

        return s3Presigner.presignGetObject(assinatura).url().toString();
    }

    private String bucket(EscopoImagem escopo) {
        return escopo.isPublico() ? bucketPublico : bucketPrivado;
    }

    private static String removerBarraFinal(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
