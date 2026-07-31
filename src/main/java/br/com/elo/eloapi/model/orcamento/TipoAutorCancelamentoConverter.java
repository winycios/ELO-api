package br.com.elo.eloapi.model.orcamento;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoAutorCancelamentoConverter implements AttributeConverter<TipoAutorCancelamento, String> {

    @Override
    public String convertToDatabaseColumn(TipoAutorCancelamento autor) {
        return autor == null ? null : autor.getDescricao();
    }

    @Override
    public TipoAutorCancelamento convertToEntityAttribute(String valor) {
        return valor == null ? null : TipoAutorCancelamento.buscarTipo(valor);
    }
}
