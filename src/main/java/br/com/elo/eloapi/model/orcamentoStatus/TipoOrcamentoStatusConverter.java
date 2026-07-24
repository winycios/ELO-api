package br.com.elo.eloapi.model.orcamentoStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoOrcamentoStatusConverter implements AttributeConverter<TipoOrcamentoStatus, String> {

    @Override
    public String convertToDatabaseColumn(TipoOrcamentoStatus tipo) {
        return tipo == null ? null : tipo.getDescricao();
    }

    @Override
    public TipoOrcamentoStatus convertToEntityAttribute(String valor) {
        return valor == null ? null : TipoOrcamentoStatus.buscarTipo(valor);
    }
}
