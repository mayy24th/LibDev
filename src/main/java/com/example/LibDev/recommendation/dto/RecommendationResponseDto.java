package com.example.LibDev.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationResponseDto {
    private Long bookId;
    private String title;
    private String author;
    private String thumbnail;
}
