package br.com.elo.eloapi.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStore.class);

    // HSET
    public static final String KEY_TEMPLATE_AUTH = "auth:user:%d:refresh-tokens";
    public static final String KEY_TEMPLATE_PROFESSIONAL_CALENDAR = "professional-calendar:professional:%d";
    public static final String KEY_TEMPLATE_PROFESSIONAL_CALENDAR_PATTERN = "dateInit:%s:dateLast:%s";
    // SET
    public static final String KEY_CATEGORIES = "categories:all";
    public static final String KEY_PROF_SERVICES = "services:profissional:%d";
    public static final String KEY_PROFESSIONAL_DETAILS = "professional-details:%d:%d";
    public static final String KEY_AVAILABLE_HOURS = "available-hours:professional:%d:service:%d:week:%s";
    // Caso um dia tiver vontade, o correto é isso virar um hset
    public static final String KEY_PROFESSIONAL_DETAILS_PATTERN = "professional-details:%d:*";
    public static final String KEY_AVAILABLE_HOURS_PATTERN = "available-hours:professional:%d:*";

    public static final Duration CACHE_DURATION = Duration.ofDays(1);
    public static final Duration AVAILABLE_TWO_MINUTES_CACHE_DURATION = Duration.ofMinutes(2);
    public static final Duration AVAILABLE_TWO_HOURS_CACHE_DURATION = Duration.ofHours(2);


    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();


    public <T> T buscarNoCache(String cacheKey, String parameter, TypeReference<T> type) {
        try {
            return findHset(cacheKey, parameter, type).orElse(null);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponível.", exception);
            return null;
        }
    }

    public <T> T buscarNoCache(String cacheKey, String parameter, Class<T> type) {
        try {
            return findHset(cacheKey, parameter, type).orElse(null);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponível.", exception);
            return null;
        }
    }

    public <T> T buscarNoCache(String cacheKey, Class<T> type) {
        try {
            return find(cacheKey, type).orElse(null);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponível.", exception);
            return null;
        }
    }

    public <T> Optional<List<T>> buscarNoCacheList(String cacheKey, Class<T> type) {
        try {
            return findList(cacheKey, type);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponível.", exception);
            return Optional.empty();
        }
    }

    public void salvarNoCache(String cacheKey, Object response, Duration duration) {
        try {
            save(cacheKey, response, duration);
        } catch (RuntimeException exception) {
            LOGGER.warn("Não foi possível armazenar no Redis.", exception);
        }
    }

    public void salvarNoCache(String cacheKey, String parameter, Object response, Duration duration) {
        try {
            saveHSet(cacheKey, parameter, response, duration);
        } catch (RuntimeException exception) {
            LOGGER.warn("Não foi possível armazenar no Redis.", exception);
        }
    }

    public void deletarNoCache(String pattern) {
        try {
            List<String> keys = new ArrayList<>();
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();

            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                cursor.forEachRemaining(keys::add);
            }

            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            LOGGER.warn("Não foi possível armazenar no Redis.", e);
        }
    }

    public void deletarNoCacheHset(String cacheKey, String parameter) {
        try {
            deleteHSetField(cacheKey, parameter);
        } catch (RuntimeException exception) {
            LOGGER.warn("Nao foi possivel remover o campo {} do HSET {}.", parameter, cacheKey, exception);
        }
    }

    public void deletarHSet(String cacheKey) {
        try {
            redisTemplate.delete(cacheKey);
        } catch (RuntimeException exception) {
            LOGGER.warn("Nao foi possivel remover o HSET {}.", cacheKey, exception);
        }
    }

    //Hash set -- lista de sets
    private void saveHSet(String key, String parameter, Object data, Duration ttl) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, parameter, toJson(data));
        hashOperations.expire(key, ttl, List.of(parameter));
    }

    private <T> Optional<T> findHset(String key, String parameter, Class<T> tClass) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(key, parameter)).map(object -> fromJson(object, tClass));
    }

    private <T> Optional<T> findHset(String key, String parameter, TypeReference<T> tClass) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(key, parameter)).map(object -> fromJson(object, tClass));
    }

    private void deleteHSetField(String key, String parameter) {
        redisTemplate.opsForHash().delete(key, parameter);
    }

    // set comum
    private void save(String key, Object data, Duration ttl) {
        redisTemplate.opsForValue().set(key, toJson(data), ttl);
    }

    private <T> Optional<T> find(String key, Class<T> type) {
        String json = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(json).map(value -> fromJson(value, type));
    }

    private <T> Optional<List<T>> findList(String key, Class<T> elementType) {
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

    private <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao desserializar o objeto armazenado no Redis.", e);
        }
    }
}
