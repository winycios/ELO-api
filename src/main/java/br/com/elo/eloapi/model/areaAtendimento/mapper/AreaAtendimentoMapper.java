package br.com.elo.eloapi.model.areaAtendimento.mapper;

import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoRS;
import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoUpdateDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class AreaAtendimentoMapper {

    public static void toUpdateEntity(AreaAtendimento areaAtendimento, AreaAtendimentoUpdateDTO dto) {

        areaAtendimento.setNmBairro(dto.getNmBairro());
        areaAtendimento.setNrLatitude(dto.getNrLatitude());
        areaAtendimento.setNrLongitude(dto.getNrLongitude());
        areaAtendimento.setNrRaio(dto.getNrRaio());
        areaAtendimento.setNmCidade(dto.getNmCidade());
        areaAtendimento.setNmEstado(dto.getNmEstado());
    }


    public static AreaAtendimento toEntity(AreaAtendimentoUpdateDTO dto) {
        AreaAtendimento areaAtendimento = new AreaAtendimento();
        toUpdateEntity(areaAtendimento, dto);
        return areaAtendimento;
    }

    public static AreaAtendimentoRS toResponse(AreaAtendimento areaAtendimento) {
        return new AreaAtendimentoRS(
                areaAtendimento.getId(),
                areaAtendimento.getNrLatitude(),
                areaAtendimento.getNrLongitude(),
                areaAtendimento.getNrRaio(),
                areaAtendimento.getNmCidade(),
                areaAtendimento.getNmBairro(),
                areaAtendimento.getNmEstado()
        );
    }
}
