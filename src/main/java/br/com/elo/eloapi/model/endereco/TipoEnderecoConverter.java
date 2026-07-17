package br.com.elo.eloapi.model.endereco;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoEnderecoConverter implements AttributeConverter<TipoEndereco, String> {

    @Override
    public String convertToDatabaseColumn(TipoEndereco tipo) {
        return tipo == null ? null : tipo.getTipoEndereco();
    }

    @Override
    public TipoEndereco convertToEntityAttribute(String valor) {
        return valor == null ? null : TipoEndereco.buscarTipo(valor);
    }
}