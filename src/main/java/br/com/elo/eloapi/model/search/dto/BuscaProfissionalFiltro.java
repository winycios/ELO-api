package br.com.elo.eloapi.model.search.dto;

public record BuscaProfissionalFiltro(
        String texto,
        Long categoriaId,
        Double avaliacaoMinima,
        Double latitude,
        Double longitude,
        Double distanciaKm,
        OrdenacaoBuscaProfissional ordenacao,
        Integer pagina,
        Integer tamanho
) {
}
