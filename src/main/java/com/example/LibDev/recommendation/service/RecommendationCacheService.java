package com.example.LibDev.recommendation.service;


import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.cache.redis.time-to-live}")
    private Long cacheTtl;

    // 데이터 조회
    public List<RecommendationResponseDto> getCachedRecommendations(String key) {
        Object cachedObject = redisTemplate.opsForValue().get(key);

        if (cachedObject == null) {
            return null;
        }

        if (cachedObject instanceof String cachedData) {
            return deserializeJsonToList(cachedData);
        }

        return Collections.emptyList();
    }

    // 데이터 저장
    public void cacheRecommendations(String key, List<RecommendationResponseDto> recommendations) {
        String jsonValue = serializeListToJson(recommendations);
        redisTemplate.opsForValue().set(key, jsonValue, cacheTtl, TimeUnit.MINUTES);
    }

    // 특정 데이터 삭제
    public void clearCache(String key) {
        redisTemplate.delete(key);
    }

    // List<RecommendationResponseDto> → JSON String 변환
    private String serializeListToJson(List<RecommendationResponseDto> recommendations) {
        try {
            return objectMapper.writeValueAsString(recommendations);
        } catch (JsonProcessingException e) {
            throw new CustomException(CustomErrorCode.CACHE_CONVERSION_FAILED);
        }
    }

    // JSON String → List<RecommendationResponseDto> 변환
    private List<RecommendationResponseDto> deserializeJsonToList(String jsonData) {
        try {
            RecommendationResponseDto[] array = objectMapper.readValue(jsonData, RecommendationResponseDto[].class);
            return Arrays.asList(array);
        } catch (JsonProcessingException e) {
            throw new CustomException(CustomErrorCode.CACHE_CONVERSION_FAILED);
        }
    }
}
