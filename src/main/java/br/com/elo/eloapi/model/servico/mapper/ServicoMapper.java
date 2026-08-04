package br.com.elo.eloapi.model.servico.mapper;

import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.servico.ServicoImagem;
import br.com.elo.eloapi.model.servico.TipoServico;
import br.com.elo.eloapi.model.servico.dto.ServicoCreateRQ;
import br.com.elo.eloapi.model.servico.dto.ServicoListaRS;
import br.com.elo.eloapi.model.servico.dto.ServicoRS;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

@Component
@AllArgsConstructor
public final class ServicoMapper {

    public static Servico toEntity(ServicoCreateRQ dto) {
        Servico servico = new Servico();
        CategoriaEspecifica categoriaEspecifica = new CategoriaEspecifica();

        categoriaEspecifica.setId(dto.idCategoriaEspecifica());

        servico.setId(dto.id());
        servico.setCategoriaEspecifica(categoriaEspecifica);
        servico.setDsDescricao(dto.dsDescricao());
        servico.setVlServico(dto.vlServico());
        servico.setTempoExperiencia(dto.tempoExperiencia());
        servico.setDsTag(dto.dsTag());
        servico.setTipoServico(TipoServico.buscarTipo(dto.tpExecucao()));

        return servico;
    }

    public static ServicoRS toResponse(Servico servico) {
        return toResponse(servico, List.of(), List.of(), UnaryOperator.identity());
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

    public static ServicoRS toResponse(Servico servico, List<ServicoImagem> imagens, List<ServicoDisponibilidade> disponibilidades, UnaryOperator<String> resolverImagem) {
        return new ServicoRS(
                servico.getId(),
                servico.getProfissional().getId(),
                new ServicoRS.ServicoCategoriaRS(
                        servico.getCategoriaEspecifica().getId(),
                        servico.getCategoriaEspecifica().getCategoriaGeral().getId()
                ),
                imagens.stream()
                        .filter(imagem -> Objects.nonNull(imagem.getChave()))
                        .map(imagem -> new ServicoRS.ServicoImagemRS(
                                imagem.getId(),
                                resolverImagem.apply(imagem.getChave()),
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
