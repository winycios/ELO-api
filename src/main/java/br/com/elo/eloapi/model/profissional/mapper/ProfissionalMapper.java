package br.com.elo.eloapi.model.profissional.mapper;

import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.areaAtendimento.mapper.AreaAtendimentoMapper;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.profissional.dto.ProfissionalRS;
import br.com.elo.eloapi.model.profissional.dto.ProfissionalUpdateDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class ProfissionalMapper {

    public static void toUpdateEntity(Profissional profissional, ProfissionalUpdateDTO dto) {

        profissional.setDsApresentacao(dto.apresentacao());
        profissional.setUriPerfil(dto.uriPerfil());
        profissional.setDsEspecialidades(dto.especialidades());
    }

    public static Profissional toEntity(ProfissionalUpdateDTO dto) {
        Profissional profissional = new Profissional();
        toUpdateEntity(profissional, dto);
        return profissional;
    }

    public static ProfissionalRS toResponse(Profissional profissional, AreaAtendimento areaAtendimento) {
        return new ProfissionalRS(
                profissional.getId(),
                profissional.getQtServicoConcluido(),
                null,
                profissional.getStDisponivel(),
                profissional.getDsApresentacao(),
                profissional.getUriPerfil(),
                profissional.getDsEspecialidades(),
                areaAtendimento == null ? null : AreaAtendimentoMapper.toResponse(areaAtendimento)
        );
    }
}
