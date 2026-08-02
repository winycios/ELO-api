package br.com.elo.eloapi.model.servico;

import java.time.LocalDateTime;
import java.util.List;

public record ProfissionalServicoRS(
        ProfissionalDetalhesRS profissional,
        ServicoOferecidoRS servicoSelecionado,
        List<ServicoOferecidoRS> servicosOferecidos,
        ResumoAvaliacoesRS resumoAvaliacoes,
        List<AvaliacaoRS> ultimasAvaliacoes
) {

    public record ProfissionalDetalhesRS(
            Long id,
            String nome,
            String fotoPerfil,
            String apresentacao,
            String especialidades,
            Double avaliacao,
            Integer quantidadeAvaliacoes,
            Integer servicosConcluidos,
            Integer tempoExperiencia,
            Double distanciaKm
    ) {
    }

    public record ServicoOferecidoRS(
            Long id,
            String nome,
            Double valor,
            String descricao,
            String pontosPrincipais,
            String tipoExecucao,
            Integer tempoExperiencia,
            CategoriaRS categoria,
            List<ImagemRS> imagens,
            List<DisponibilidadeRS> disponibilidades
    ) {
    }

    public record CategoriaRS(
            Long categoriaGeralId,
            String categoriaGeral,
            Long categoriaEspecificaId,
            String categoriaEspecifica
    ) {
    }

    public record ImagemRS(Long id, String url, Integer ordem) {
    }

    public record DisponibilidadeRS(
            Long id,
            Integer diaSemana,
            String horaInicio,
            String horaFim
    ) {
    }

    public record ResumoAvaliacoesRS(
            Double media,
            Integer quantidade,
            Double percentualPositivas
    ) {
    }

    public record AvaliacaoRS(
            Long id,
            Long avaliadorId,
            String avaliador,
            String fotoAvaliador,
            Integer nota,
            String comentario,
            LocalDateTime dataCriacao
    ) {
    }
}
