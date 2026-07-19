package br.com.elo.eloapi.model.search;

import java.util.List;

public record ProfissionalSearchDocument(
        Long profissionalId,
        String nome,
        String fotoPerfil,
        Boolean habilitado,
        Boolean disponivel,
        Double avaliacao,
        Integer quantidadeAvaliacoes,
        Integer servicosConcluidos,
        LocalizacaoSearch localizacao,
        Integer raioAtendimentoKm,
        String cidade,
        String estado,
        String bairro,
        List<ServicoSearch> servicos
) {

    public record LocalizacaoSearch(
            Double lat,
            Double lon
    ) {
    }

    public record ServicoSearch(
            Long servicoId,
            Long categoriaGeralId,
            String categoriaGeral,
            Long categoriaEspecificaId,
            String categoriaEspecifica,
            String descricao,
            String tags,
            Double preco,
            String tipoExecucao,
            Integer tempoExperiencia,
            Boolean ativo
    ) {
    }
}
