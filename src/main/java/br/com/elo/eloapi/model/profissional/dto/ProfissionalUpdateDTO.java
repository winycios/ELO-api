package br.com.elo.eloapi.model.profissional.dto;

import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoUpdateRQ;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfissionalUpdateDTO(

        @Size(max = 200) String apresentacao,
        @Size(max = 500) String chaveImagem,
        @Size(max = 200) String especialidades,
        @NotNull AreaAtendimentoUpdateRQ areaAtendimentoUpdateRQ
) {
}
