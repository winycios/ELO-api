package br.com.elo.eloapi.model.endereco.dto;

public record EnderecoRS(Long id,
                         String nmApelido,
                         String tipoEndereco,
                         String cep,
                         String rua,
                         Integer nrRua,
                         String complemento,
                         String bairro,
                         String cidade,
                         String estado,
                         Double latitude,
                         Double longitude,
                         Boolean stPrincipal) {
}
