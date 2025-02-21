package com.example.LibDev.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void setRefreshToken(String email, String refreshToken, long refreshTokenExpireTime) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(refreshToken.getClass()));
        redisTemplate.opsForValue().set(email, refreshToken, refreshTokenExpireTime, TimeUnit.MINUTES);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void delRefreshToken(String email) {
        redisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(email));
    }

    public void setBlackList(String accessToken, String msg, long expireTime) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(msg.getClass()));
        redisTemplate.opsForValue().set(accessToken, msg, expireTime, TimeUnit.MINUTES);
    }
}
