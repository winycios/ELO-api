package br.com.elo.eloapi.model.publicacao.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PublicacaoFeedRS(
        Long id,
        String descricao,
        LocalDateTime publicadoEm,
        Long categoriaId,
        String categoriaNome,
        Long profissionalId,
        String profissionalNome,
        String profissionalFotoUrl,
        List<PublicacaoImagemRS> imagens,
        long quantidadeCurtidas,
        long quantidadeComentarios,
        Boolean isCurtido
) {
}
