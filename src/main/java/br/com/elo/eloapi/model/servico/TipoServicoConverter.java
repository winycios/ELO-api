package br.com.elo.eloapi.model.servico;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoServicoConverter implements AttributeConverter<TipoServico, String> {

    @Override
    public String convertToDatabaseColumn(TipoServico tipo) {
        return tipo == null ? null : tipo.getTipoServico();
    }

    @Override
    public TipoServico convertToEntityAttribute(String valor) {
        return valor == null ? null : TipoServico.buscarTipo(valor);
    }
}