package br.com.elo.eloapi.model.servico.mapper;

import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.servico.ServicoImagem;
import br.com.elo.eloapi.model.servico.TipoServico;
import br.com.elo.eloapi.model.servico.dto.ServicoCreateDTO;
import br.com.elo.eloapi.model.servico.dto.ServicoListaRS;
import br.com.elo.eloapi.model.servico.dto.ServicoRS;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public final class ServicoMapper {

    public static Servico toEntity(ServicoCreateDTO dto) {
        Servico servico = new Servico();
        CategoriaEspecifica categoriaEspecifica = new CategoriaEspecifica();

        categoriaEspecifica.setId(dto.getIdCategoriaEspecifica());

        servico.setId(dto.getId());
        servico.setCategoriaEspecifica(categoriaEspecifica);
        servico.setDsDescricao(dto.getDsDescricao());
        servico.setVlServico(dto.getVlServico());
        servico.setTempoExperiencia(dto.getTempoExperiencia());
        servico.setDsTag(dto.getDsTag());
        servico.setTipoServico(TipoServico.buscarTipo(dto.getTpExecucao()));

        return servico;
    }

    public static ServicoRS toResponse(Servico servico) {
        return toResponse(servico, List.of(), List.of());
    }

    public static ServicoListaRS toListResponse(Servico servico) {
        return new ServicoListaRS(
                servico.getId(),
                new ServicoRS.ServicoCategoriaRS(
                        servico.getCategoriaEspecifica().getId(),
                        servico.getCategoriaEspecifica().getCategoriaGeral().getId()
                ),
                servico.getDsDescricao(),
                servico.getVlServico(),
                servico.getDsTag(),
                servico.getTipoServico().getTipoServico()
        );
    }

    public static ServicoRS toResponse(Servico servico, List<ServicoImagem> imagens, List<ServicoDisponibilidade> disponibilidades) {
        return new ServicoRS(
                servico.getId(),
                servico.getProfissional().getId(),
                new ServicoRS.ServicoCategoriaRS(
                        servico.getCategoriaEspecifica().getId(),
                        servico.getCategoriaEspecifica().getCategoriaGeral().getId()
                ),
                imagens.stream()
                        .map(imagem -> new ServicoRS.ServicoImagemRS(
                                imagem.getId(),
                                imagem.getUrl(),
                                imagem.getOrdem()
                        ))
                        .toList(),
                disponibilidades.stream()
                        .map(disponibilidade -> new ServicoRS.ServicoDisponibilidadeRS(
                                disponibilidade.getId(),
                                disponibilidade.getDiaSemana(),
                                disponibilidade.getHrInicio(),
                                disponibilidade.getHrFim()
                        ))
                        .toList(),
                servico.getDsDescricao(),
                servico.getVlServico(),
                servico.getTempoExperiencia(),
                servico.getDsTag(),
                servico.getTipoServico().getTipoServico()
        );
    }

    public static List<ServicoRS> toResponse(List<Servico> servicoList) {
        return servicoList.stream().map(ServicoMapper::toResponse).toList();
    }
}
