package br.com.elo.eloapi.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisStore {

    public static final String KEY_TEMPLATE_AUTH = "auth:user:%d:refresh-tokens";
    public static final String KEY_CATEGORIES = "categories:all";
    public static final String KEY_PROF_SERVICES = "services:profissional:%d";
    public static final String KEY_PROFESSIONAL_DETAILS = "professional-details:%d:%d";
    public static final String KEY_PROFESSIONAL_DETAILS_PATTERN = "professional-details:%d:*";

    public static final Duration CACHE_DURATION = Duration.ofDays(1);


    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    //Hash set -- lista de sets
    public void saveHSet(String key, String parameter, Object data, Duration ttl) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, parameter, toJson(data));
        hashOperations.expire(key, ttl, List.of(parameter));
    }

    public <T> Optional<T> findHset(String key, String parameter, Class<T> tClass) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(key, parameter)).map(object -> fromJson(object, tClass));
    }

    // set comum
    public void save(String key, Object data, Duration ttl) {
        redisTemplate.opsForValue().set(key, toJson(data), ttl);
    }

    public <T> Optional<T> find(String key, Class<T> type) {
        String json = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(json).map(value -> fromJson(value, type));
    }

    public <T> Optional<List<T>> findList(String key, Class<T> elementType) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            var listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
            return Optional.of(objectMapper.readValue(json, listType));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao desserializar a lista armazenada no Redis.", e);
        }
    }

    public void deleteByPattern(String pattern) {
        List<String> keys = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(keys::add);
        }

        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
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