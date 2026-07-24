package br.com.elo.eloapi.model.areaAtendimento.mapper;

import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoRS;
import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoUpdateRQ;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class AreaAtendimentoMapper {

    public static void toUpdateEntity(AreaAtendimento areaAtendimento, AreaAtendimentoUpdateRQ dto) {

        areaAtendimento.setNmBairro(dto.nmBairro());
        areaAtendimento.setNrLatitude(dto.nrLatitude());
        areaAtendimento.setNrLongitude(dto.nrLongitude());
        areaAtendimento.setNrRaio(dto.nrRaio());
        areaAtendimento.setNmCidade(dto.nmCidade());
        areaAtendimento.setNmEstado(dto.nmEstado());
    }


    public static AreaAtendimento toEntity(AreaAtendimentoUpdateRQ dto) {
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
