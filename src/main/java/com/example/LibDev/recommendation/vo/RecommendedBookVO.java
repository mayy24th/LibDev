package com.example.LibDev.recommendation.vo;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendedBookVO {
    private Long bookId;
    private String title;
    private String author;
    private String thumbnail;

    public RecommendationResponseDto toDto() {
        return RecommendationResponseDto.builder()
                .bookId(this.bookId)
                .title(this.title)
                .author(this.author)
                .thumbnail(this.thumbnail)
                .build();
    }
}