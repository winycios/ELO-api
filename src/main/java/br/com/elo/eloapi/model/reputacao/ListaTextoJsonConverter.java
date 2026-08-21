package br.com.elo.eloapi.model.reputacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Converter
public class ListaTextoJsonConverter implements AttributeConverter<List<String>, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListaTextoJsonConverter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> TIPO = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(List<String> valores) {
        if (valores == null || valores.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(valores);
        } catch (JsonProcessingException excecao) {
            throw new IllegalStateException("Falha ao serializar a lista de aspectos.", excecao);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return MAPPER.readValue(json, TIPO);
        } catch (JsonProcessingException excecao) {
            LOGGER.warn("Conteudo JSON invalido na reputacao de PLN: {}", json, excecao);
            return List.of();
        }
    }
}
