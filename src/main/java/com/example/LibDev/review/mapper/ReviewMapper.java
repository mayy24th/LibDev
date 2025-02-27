package com.example.LibDev.review.mapper;

import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.entity.Review;
import com.example.LibDev.user.entity.type.Role;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto.Response toDto(Review review, String userEmail) {
        return ReviewDto.Response.builder()
                .id(review.getId())
                .email(review.getUser().getEmail())
                .userName(review.getUser().getName())
                .bookName(review.getBook().getTitle())
                .thumbnail(review.getBook().getThumbnail())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .isOwner((userEmail != null && userEmail.equals(review.getUser().getEmail()))
                        || review.getUser().getRole() == Role.ADMIN)
                .bookId(review.getBook().getBookId())
                .build();
    }

    public List<ReviewDto.Response> toDtoList(List<Review> reviews, String userEmail) {
        return reviews.stream()
                .map(review -> toDto(review, userEmail))
                .collect(Collectors.toList());
    }
}