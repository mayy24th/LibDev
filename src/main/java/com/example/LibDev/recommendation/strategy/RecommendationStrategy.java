package com.example.LibDev.recommendation.strategy;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import java.util.List;

public interface RecommendationStrategy {
    List<RecommendationResponseDto> recommend(Long bookId, String email);
}