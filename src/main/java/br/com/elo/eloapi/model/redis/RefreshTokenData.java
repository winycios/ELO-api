package br.com.elo.eloapi.model.redis;

public record RefreshTokenData(String tokenId, String tokenHash, Long userId) {
}