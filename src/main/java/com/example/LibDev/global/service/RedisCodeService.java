package com.example.LibDev.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCodeService {
    private final RedisTemplate<String, String> redisTemplate;
    private final static String CODE_PREFIX = "code:";

    public void saveCode(String email, String code, long expireTime) {
        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, expireTime, TimeUnit.MINUTES);
    }

    public String getCode(String email) {
        return redisTemplate.opsForValue().get(CODE_PREFIX + email);
    }

    public void deleteCode(String email) {
        redisTemplate.delete(CODE_PREFIX + email);
    }

    public boolean existCode(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(CODE_PREFIX + email));
    }
}
