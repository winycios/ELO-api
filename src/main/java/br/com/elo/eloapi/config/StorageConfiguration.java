package br.com.elo.eloapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class StorageConfiguration {

    @Bean(destroyMethod = "close")
    public S3Client s3Client(@Value("${elo.storage.endpoint}") String endpoint, @Value("${elo.storage.region}") String region, @Value("${elo.storage.access-key}") String accessKey, @Value("${elo.storage.secret-key}") String secretKey, @Value("${elo.storage.path-style:true}") boolean pathStyle) {
        return S3Client.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region)).credentialsProvider(credenciais(accessKey, secretKey)).forcePathStyle(pathStyle).build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner(@Value("${elo.storage.endpoint}") String endpoint, @Value("${elo.storage.region}") String region, @Value("${elo.storage.access-key}") String accessKey, @Value("${elo.storage.secret-key}") String secretKey, @Value("${elo.storage.path-style:true}") boolean pathStyle) {
        return S3Presigner.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region)).credentialsProvider(credenciais(accessKey, secretKey)).serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder().pathStyleAccessEnabled(pathStyle).build()).build();
    }

    private StaticCredentialsProvider credenciais(String accessKey, String secretKey) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }
}
