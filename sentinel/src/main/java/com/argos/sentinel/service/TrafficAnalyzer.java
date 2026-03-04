package com.argos.sentinel.service;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TrafficAnalyzer {

    private final RedisService redisService;
    private final StringRedisTemplate redisTemplate;

    private static final int LIMIT = 50; 
    private static final int WINDOW_SECONDS = 10;
    private static final String RATE_PREFIX = "rate:ip:";

    public TrafficAnalyzer(RedisService redisService, StringRedisTemplate redisTemplate) {
        this.redisService = redisService;
        this.redisTemplate = redisTemplate;
    }

    public boolean processAndCheckLimit(String ip) {
        if (redisService.isBanned(ip)) return true;

        String key = RATE_PREFIX + ip;
        
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }

        if (currentCount != null && currentCount > LIMIT) {
            redisService.banIp(ip, 10);
            return true;
        }

        return false;
    }
}