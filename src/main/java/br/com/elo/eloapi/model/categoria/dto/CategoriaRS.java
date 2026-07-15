package br.com.elo.eloapi.model.categoria.dto;

import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;

import java.util.List;

public record CategoriaRS(String categoriaGeral, List<CategoriaEspecifica> categoriaEspecificaList) {
}
