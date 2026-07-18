package br.com.elo.eloapi.model.areaAtendimento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaAtendimentoUpdateDTO {

    private Long id;

    @NotNull
    private Double nrLatitude;

    @NotNull
    private Double nrLongitude;

    @NotNull
    private Integer nrRaio;

    @NotBlank
    private String nmCidade;

    @NotBlank
    private String nmEstado;

    @NotBlank
    private String nmBairro;
}

