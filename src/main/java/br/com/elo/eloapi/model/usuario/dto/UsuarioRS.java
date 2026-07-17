package br.com.elo.eloapi.model.usuario.dto;

public record UsuarioRS(Long id, String nome, String email, String telefone, String telefoneZap, Long qtdPedido,
                        Long qtdConcluido, Double avaliacaoGeral) {
}
