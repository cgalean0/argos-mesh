package com.argos.sentinel.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TrafficAnalyzer {

    private final RedisService redisService;
    private final StringRedisTemplate redisTemplate;

    public TrafficAnalyzer(RedisService redisService, StringRedisTemplate redisTemplate) {
        this.redisService = redisService;
        this.redisTemplate = redisTemplate;
    }
    private static final int WINDOW_MILIS = 10000;
    private static final int LIMIT = 50; 
    
    public boolean processAndCheckLimit(String ip) {
        // 1. Si está baneada...
        if (redisService.isBanned(ip)) return true;
        // 2. ZADD — agregar timestamp actual
        String RATE_PREFIX = "rate:ip:";
        String key = RATE_PREFIX + ip;
        long now = Instant.now().toEpochMilli();
        long windowStart = now - WINDOW_MILIS;
        redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), now);
        // 3. ZREMRANGEBYSCORE — eliminar fuera de la ventana
        redisTemplate.opsForZSet().removeRangeByScore(key, windowStart, now);
        // 4. ZCARD — contar requests en la ventana
        long requests = redisTemplate.opsForZSet().zCard(key);
        // 5. Si supera el límite...
        if (requests > LIMIT) return true;
        return false;
    }
    
}