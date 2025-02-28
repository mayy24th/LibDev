package com.example.LibDev.review.mapper;

import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.entity.Review;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto.Response toDto(Review review, User user) {
        return ReviewDto.Response.builder()
                .id(review.getId())
                .email(review.getUser().getEmail())
                .userName(review.getUser().getName())
                .bookName(review.getBook().getTitle())
                .thumbnail(review.getBook().getThumbnail())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .isOwner(user != null && (user.getEmail().equals(review.getUser().getEmail()) || user.getRole() == Role.ADMIN))
                .bookId(review.getBook().getBookId())
                .build();
    }

    public List<ReviewDto.Response> toDtoList(List<Review> reviews, User user) {
        return reviews.stream()
                .map(review -> toDto(review, user))
                .collect(Collectors.toList());
    }
}