package com.example.LibDev.review.mapper;

import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto.Response toDto(Review review) {
        return ReviewDto.Response.builder()
                .id(review.getId())
                .userName(review.getUser().getName())
                .bookName(review.getBook().getTitle())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}