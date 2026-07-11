package br.com.elo.eloapi.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class RefreshTokenStore {

    public static final String KEY_TEMPLATE_AUTH = "auth:user:%d:refresh-tokens";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveHSet(String key, String parameter, Object data, Duration ttl) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, parameter, toJson(data));
        hashOperations.expire(key, ttl, List.of(parameter));
    }

    public <T> Optional<T> findHset(String key, String parameter, Class<T> tClass) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(key, parameter)).map(object -> fromJson(object, tClass));
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar o objeto.", e);
        }
    }

    private <T> T fromJson(String json, Class<T> classObject) {
        try {
            return objectMapper.readValue(json, classObject);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao desserializar o refresh token.", e);
        }
    }
}