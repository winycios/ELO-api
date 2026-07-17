package br.com.elo.eloapi.integration.cep;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.model.endereco.dto.CepResponse;
import br.com.elo.eloapi.model.endereco.dto.Coordenadas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AwesomeApiCepClient {

    private final RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(AwesomeApiCepClient.class);

    public AwesomeApiCepClient(RestClient.Builder restClientBuilder, @Value("${integration.awesome-api-cep.url:https://cep.awesomeapi.com.br/json}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public Coordenadas buscarCoordenadas(String cep) {
        String cepNormalizado = normalizarCep(cep);

        try {
            CepResponse response = restClient.get().uri("/{cep}", cepNormalizado).retrieve().body(CepResponse.class);

            if (response == null || response.lat() == null || response.lng() == null) {
                return new Coordenadas(null, null);
            }

            return new Coordenadas(Double.parseDouble(response.lat()), Double.parseDouble(response.lng()));
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new Coordenadas(null, null);
        }
    }

    private String normalizarCep(String cep) {
        if (cep == null) {
            throw new BadRequestException("O CEP deve possuir 8 dígitos");
        }

        String cepNormalizado = cep.replaceAll("\\D", "");
        if (cepNormalizado.length() != 8) {
            throw new BadRequestException("O CEP deve possuir 8 dígitos");
        }
        return cepNormalizado;
    }
}
