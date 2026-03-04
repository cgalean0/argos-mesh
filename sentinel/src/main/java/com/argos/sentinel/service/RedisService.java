package com.argos.sentinel.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:ip:";

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // Method that block an IP
    public void banIp(String ipAddress, long durationMinutes) {
        //save the ip with de value "BANNED" and one TTL
        redisTemplate.opsForValue().set(
            BLACKLIST_PREFIX + ipAddress,
            "BANNED",
            Duration.ofMinutes(durationMinutes)
        );
    }

    public boolean isBanned(String ipAddres) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + ipAddres)); 
    }
}
