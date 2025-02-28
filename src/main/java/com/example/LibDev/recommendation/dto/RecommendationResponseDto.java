package com.example.LibDev.recommendation.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationResponseDto {
    private Long bookId;
    private String title;
    private String author;
    private String thumbnail;
    private String publisher;
    private String contents;
    private LocalDate publishedDate;
}
