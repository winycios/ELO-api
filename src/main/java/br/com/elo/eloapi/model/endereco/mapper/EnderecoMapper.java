package br.com.elo.eloapi.model.endereco.mapper;


import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.endereco.TipoEndereco;
import br.com.elo.eloapi.model.endereco.dto.EnderecoCreateDTO;
import br.com.elo.eloapi.model.endereco.dto.EnderecoRS;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EnderecoMapper {

    public static Endereco toEntity(EnderecoCreateDTO dto) {
        Endereco endereco = new Endereco();

        endereco.setId(dto.getId());
        endereco.setNmApelido(dto.getNmApelido());
        endereco.setNmRua(dto.getRua());
        endereco.setNmComplemento(dto.getComplemento());
        endereco.setNmBairro(dto.getBairro());
        endereco.setNmCidade(dto.getCidade());
        endereco.setNmEstado(dto.getEstado());
        endereco.setNrCep(dto.getCep());
        endereco.setTipoEndereco(TipoEndereco.buscarTipo(dto.getTipoEndereco()));
        endereco.setNrRua(dto.getNrRua());

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
