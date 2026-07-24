package br.com.elo.eloapi.model.orcamentoStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TipoOrcamentoStatus {

    PENDENTE("pendente"),
    EM_ANDAMENTO("em_andamento"),
    ORCAMENTO_FINAL("orcamento_final"),
    APROVADO("aprovado"),
    CONCLUIDO("concluido"),
    CANCELADO("cancelado");

    private final String descricao;

    public static TipoOrcamentoStatus buscarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("O tipo de status não pode ser vazio");
        }

        String tipoNormalizado = tipo.trim();
        return Arrays.stream(values()).filter(tipoOrcamento -> tipoOrcamento.descricao.equalsIgnoreCase(tipoNormalizado) || tipoOrcamento.name().equalsIgnoreCase(tipoNormalizado)).findFirst().orElseThrow(() -> new IllegalArgumentException("Tipo de status inválido: " + tipo));
    }

    public boolean isPendente() {
        return this == PENDENTE;
    }

    public boolean isEmAndamento() {
        return this == EM_ANDAMENTO;
    }

    public boolean isOrcamentoFinal() {
        return this == ORCAMENTO_FINAL;
    }

    public boolean isAprovado() {
        return this == APROVADO;
    }

    public boolean isConcluido() {
        return this == CONCLUIDO;
    }

    public boolean isCancelado() {
        return this == CANCELADO;
    }
}
