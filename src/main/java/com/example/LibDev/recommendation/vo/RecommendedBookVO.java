package com.example.LibDev.recommendation.vo;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendedBookVO {
    private Long bookId;
    private String title;
    private String author;
    private String thumbnail;
    private String publisher;
    private String contents;
    private LocalDate publishedDate;

    public RecommendationResponseDto toDto() {
        return RecommendationResponseDto.builder()
                .bookId(this.bookId)
                .title(this.title)
                .author(this.author)
                .thumbnail(this.thumbnail)
                .publisher(this.publisher)
                .contents(this.contents)
                .publishedDate(this.publishedDate)
                .build();
    }
}