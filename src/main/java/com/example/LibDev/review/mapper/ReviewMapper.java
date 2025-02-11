package com.example.LibDev.review.mapper;

import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto.Response toDto(Review review) {
        return ReviewDto.Response.builder()
                .id(review.getId())
                // TODO : 유저, 도서정보 id -> 유저, 도서 객체
                // .user(UserDto.Response.builder()
                //      .id(review.getUser().getId())
                //      .name(review.getUser().getName())
                //      .build())
                // .book(BookDto.Response.builder()
                //      .id(review.getBook().getId())
                //      .title(review.getBook().getTitle())
                //      .build())
                .userId(review.getUser().getId())
                .bookId(review.getBook().getBookId())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}