package com.example.demo.common.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 프리티어에는 redis까지 올리기엔 너무 무거워서 주석처리, 액세스 토큰 유효시간 길게 설정
 */

@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ValueOperations<String, String> valueOps;
    public RedisService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.valueOps = redisTemplate.opsForValue();
    }


    public <T> T get(@NonNull String key, Class<T> clazz) {
        if(true) return null;
        try {
            String value = valueOps.get(key);
            if (clazz == String.class) {
                return (T) value;
            }
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize value from Redis", e);
        }
    }

    public void set(@NonNull String key, @NonNull Object value, Duration duration) {
        if(true) return;
        try {
            if (value.getClass() == String.class) {
                valueOps.set(key, (String) value, duration);
            } else {
                valueOps.set(key, objectMapper.writeValueAsString(value), duration);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize value to Redis", e);
        }
    }

    public void set(@NonNull String key, @NonNull Object value) {
        try {
            if (value.getClass() == String.class) {
                valueOps.set(key, (String) value);
            } else {
                valueOps.set(key, objectMapper.writeValueAsString(value));
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize value to Redis", e);
        }
    }

    public void delete(String key) {
        if(true) return;
        redisTemplate.delete(key);
    }
}
