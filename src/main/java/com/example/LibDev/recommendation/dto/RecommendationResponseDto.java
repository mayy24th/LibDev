package com.example.LibDev.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationResponseDto {
    private Long bookId;
    private String title;
    private String author;
    private String thumbnail;
}
