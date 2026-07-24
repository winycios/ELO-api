package br.com.elo.eloapi.model.profissional.dto;

import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoUpdateDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfissionalUpdateDTO(

        @Size(max = 200) String apresentacao,
        @Size(max = 200) String uriPerfil,
        @Size(max = 200) String especialidades,
        @NotNull AreaAtendimentoUpdateDTO areaAtendimentoUpdateDTO
) {
}
