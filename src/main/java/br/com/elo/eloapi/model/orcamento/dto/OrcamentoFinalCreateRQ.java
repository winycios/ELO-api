package br.com.elo.eloapi.model.orcamento.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record OrcamentoFinalCreateRQ(
        @NotNull
        @Future
        LocalDateTime inicioProposto,

        @NotNull
        @Future
        LocalDateTime fimProposto,

        @Size(max = 200)
        String observacaoProfissional,

        @NotEmpty
        @Size(max = 20)
        List<@Valid CustoRQ> custos
) {

    public record CustoRQ(
            @NotBlank
            @Size(max = 45)
            String descricao,

            @NotNull
            @DecimalMin(value = "0.01")
            @Digits(integer = 10, fraction = 2)
            Double valor
    ) {
    }
}
