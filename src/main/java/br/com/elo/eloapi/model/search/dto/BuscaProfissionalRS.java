package br.com.elo.eloapi.model.search.dto;

import java.util.List;

public record BuscaProfissionalRS(
        List<ProfissionalBuscaRS> profissionais,
        Long total,
        Integer pagina,
        Integer tamanho
) {

    public record ProfissionalBuscaRS(
            Long profissionalId,
            String nome,
            String fotoPerfil,
            Double avaliacao,
            Integer quantidadeAvaliacoes,
            Integer servicosConcluidos,
            Boolean disponivel,
            Double distanciaKm,
            Double precoInicial,
            String cidade,
            String estado,
            String bairro,
            List<ServicoBuscaRS> servicos
    ) {
    }

    public record ServicoBuscaRS(
            Long servicoId,
            Long categoriaGeralId,
            String categoriaGeral,
            Long categoriaEspecificaId,
            String categoriaEspecifica,
            String descricao,
            Double preco,
            String tipoExecucao
    ) {
    }
}
