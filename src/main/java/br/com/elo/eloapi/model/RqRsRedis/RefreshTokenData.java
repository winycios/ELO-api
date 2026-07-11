package br.com.elo.eloapi.model.RqRsRedis;

public record RefreshTokenData(String tokenId, String tokenHash, Long userId) {
}