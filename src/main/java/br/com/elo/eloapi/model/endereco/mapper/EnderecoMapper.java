package br.com.elo.eloapi.model.endereco.mapper;


import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.endereco.TipoEndereco;
import br.com.elo.eloapi.model.endereco.dto.EnderecoCreateRQ;
import br.com.elo.eloapi.model.endereco.dto.EnderecoRS;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public final class EnderecoMapper {

    public static Endereco toEntity(EnderecoCreateRQ dto) {
        Endereco endereco = new Endereco();

        endereco.setId(dto.id());
        endereco.setNmApelido(dto.nmApelido());
        endereco.setNmRua(dto.rua());
        endereco.setNmComplemento(dto.complemento());
        endereco.setNmBairro(dto.bairro());
        endereco.setNmCidade(dto.cidade());
        endereco.setNmEstado(dto.estado());
        endereco.setNrCep(dto.cep());
        endereco.setTipoEndereco(TipoEndereco.buscarTipo(dto.tipoEndereco()));
        endereco.setNrRua(dto.nrRua());

        return endereco;
    }

    public static EnderecoRS toResponse(Endereco endereco) {
        return new EnderecoRS(
                endereco.getId(),
                endereco.getNmApelido(),
                endereco.getTipoEndereco().getTipoEndereco(),
                endereco.getNrCep(),
                endereco.getNmRua(),
                endereco.getNrRua(),
                endereco.getNmComplemento(),
                endereco.getNmBairro(),
                endereco.getNmCidade(),
                endereco.getNmEstado(),
                endereco.getNrLatitude(),
                endereco.getNrLongitude(),
                endereco.getStPrincipal()
        );
    }

    public static List<EnderecoRS> toResponse(List<Endereco> enderecoList) {
        return enderecoList.stream().map(EnderecoMapper::toResponse).toList();
    }
}
