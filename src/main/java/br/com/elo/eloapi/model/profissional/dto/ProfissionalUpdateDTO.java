package br.com.elo.eloapi.model.profissional.dto;

import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoUpdateDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfissionalUpdateDTO {

    @Size(max = 200)
    private String apresentacao;

    @Size(max = 200)
    private String uriPerfil;

    @Size(max = 200)
    private String especialidades;

    @NotNull
    private AreaAtendimentoUpdateDTO areaAtendimentoUpdateDTO;
}
