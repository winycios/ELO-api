package br.com.elo.eloapi.model.publicacao.dto;

import java.util.List;

public record CursorPageRS<T>(List<T> items, String nextCursor, boolean hasNext) {
}
