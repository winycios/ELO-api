package br.com.elo.eloapi.model.servico.mapper;

import br.com.elo.eloapi.model.avaliacao.AvaliacaoReserva;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.servico.ProfissionalServicoRS;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.servico.ServicoImagem;
import br.com.elo.eloapi.model.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProfissionalServicoMapper {

    public ProfissionalServicoRS toResponse(
            Profissional profissional,
            Servico servicoSelecionado,
            List<Servico> servicos,
            Map<Long, List<ServicoImagem>> imagensPorServico,
            Map<Long, List<ServicoDisponibilidade>> disponibilidadesPorServico,
            List<AvaliacaoReserva> ultimasAvaliacoes,
            Double percentualAvaliacoesPositivas
    ) {
        Map<Long, ProfissionalServicoRS.ServicoOferecidoRS> servicosPorId = servicos.stream()
                .map(servico -> toServicoResponse(
                        servico,
                        imagensPorServico.getOrDefault(servico.getId(), List.of()),
                        disponibilidadesPorServico.getOrDefault(servico.getId(), List.of())
                ))
                .collect(Collectors.toMap(
                        ProfissionalServicoRS.ServicoOferecidoRS::id,
                        Function.identity()
                ));

        return new ProfissionalServicoRS(
                toProfissionalResponse(profissional, servicos),
                servicosPorId.get(servicoSelecionado.getId()),
                servicos.stream().map(servico -> servicosPorId.get(servico.getId())).toList(),
                toResumoAvaliacoesResponse(profissional, percentualAvaliacoesPositivas),
                ultimasAvaliacoes.stream().map(this::toAvaliacaoResponse).toList()
        );
    }

    public ProfissionalServicoRS comDistancia(
            ProfissionalServicoRS response,
            Double distanciaKm
    ) {
        ProfissionalServicoRS.ProfissionalDetalhesRS profissional = response.profissional();
        var profissionalComDistancia = new ProfissionalServicoRS.ProfissionalDetalhesRS(
                profissional.id(),
                profissional.nome(),
                profissional.fotoPerfil(),
                profissional.apresentacao(),
                profissional.especialidades(),
                profissional.avaliacao(),
                profissional.quantidadeAvaliacoes(),
                profissional.servicosConcluidos(),
                profissional.tempoExperiencia(),
                distanciaKm
        );

        return new ProfissionalServicoRS(
                profissionalComDistancia,
                response.servicoSelecionado(),
                response.servicosOferecidos(),
                response.resumoAvaliacoes(),
                response.ultimasAvaliacoes()
        );
    }

    public ProfissionalServicoRS.ProfissionalDetalhesRS toProfissionalResponse(
            Profissional profissional,
            List<Servico> servicos
    ) {
        Integer maiorExperiencia = servicos.stream()
                .map(Servico::getTempoExperiencia)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);
        String fotoPerfil = profissional.getUriPerfil() != null
                ? profissional.getUriPerfil()
                : profissional.getUsuario().getUriPerfil();

        return new ProfissionalServicoRS.ProfissionalDetalhesRS(
                profissional.getId(),
                profissional.getUsuario().nomeCompleto(),
                fotoPerfil,
                profissional.getDsApresentacao(),
                profissional.getDsEspecialidades(),
                profissional.getUsuario().getQtAvaliacaoGeral(),
                valorOuZero(profissional.getUsuario().getQtAvalicaoes()),
                valorOuZero(profissional.getQtServicoConcluido()),
                maiorExperiencia,
                null
        );
    }

    public ProfissionalServicoRS.ServicoOferecidoRS toServicoResponse(
            Servico servico,
            List<ServicoImagem> imagens,
            List<ServicoDisponibilidade> disponibilidades
    ) {
        var categoriaEspecifica = servico.getCategoriaEspecifica();
        var categoriaGeral = categoriaEspecifica.getCategoriaGeral();

        return new ProfissionalServicoRS.ServicoOferecidoRS(
                servico.getId(),
                categoriaEspecifica.getNmCategoria(),
                servico.getVlServico(),
                servico.getDsDescricao(),
                servico.getDsTag(),
                servico.getTipoServico() == null ? null : servico.getTipoServico().getTipoServico(),
                servico.getTempoExperiencia(),
                new ProfissionalServicoRS.CategoriaRS(
                        categoriaGeral.getId(),
                        categoriaGeral.getNmCategoria(),
                        categoriaEspecifica.getId(),
                        categoriaEspecifica.getNmCategoria()
                ),
                imagens.stream().map(this::toImagemResponse).toList(),
                disponibilidades.stream().map(this::toDisponibilidadeResponse).toList()
        );
    }

    public ProfissionalServicoRS.AvaliacaoRS toAvaliacaoResponse(AvaliacaoReserva avaliacao) {
        Usuario avaliador = avaliacao.getAvaliador();
        return new ProfissionalServicoRS.AvaliacaoRS(
                avaliacao.getId(),
                avaliador.getId(),
                avaliador.nomeCompleto(),
                avaliador.getUriPerfil(),
                avaliacao.getNota(),
                avaliacao.getComentario()
        );
    }

    private ProfissionalServicoRS.ResumoAvaliacoesRS toResumoAvaliacoesResponse(
            Profissional profissional,
            Double percentualAvaliacoesPositivas
    ) {
        return new ProfissionalServicoRS.ResumoAvaliacoesRS(
                profissional.getUsuario().getQtAvaliacaoGeral(),
                valorOuZero(profissional.getUsuario().getQtAvalicaoes()),
                percentualAvaliacoesPositivas
        );
    }

    private ProfissionalServicoRS.ImagemRS toImagemResponse(ServicoImagem imagem) {
        return new ProfissionalServicoRS.ImagemRS(
                imagem.getId(),
                imagem.getUrl(),
                imagem.getOrdem()
        );
    }

    private ProfissionalServicoRS.DisponibilidadeRS toDisponibilidadeResponse(
            ServicoDisponibilidade disponibilidade
    ) {
        return new ProfissionalServicoRS.DisponibilidadeRS(
                disponibilidade.getId(),
                disponibilidade.getDiaSemana(),
                disponibilidade.getHrInicio() == null
                        ? null
                        : disponibilidade.getHrInicio().toString(),
                disponibilidade.getHrFim() == null
                        ? null
                        : disponibilidade.getHrFim().toString()
        );
    }

    private Integer valorOuZero(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
