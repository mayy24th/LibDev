package com.example.LibDev.review.controller;

import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.dto.ReviewDto.Response;
import com.example.LibDev.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewAPIController {
    private final ReviewService reviewService;

    /** 리뷰 저장 **/
    @PostMapping
    public ResponseEntity<Void> saveReview(@Valid @RequestBody ReviewDto.SaveRequest saveRequestDto){
        reviewService.saveReview(saveRequestDto);
        return ResponseEntity.ok().build();
    }

    /** 리뷰 삭제 **/
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId){
        reviewService.deleteReview(new ReviewDto.DeleteRequest(reviewId));
        return ResponseEntity.noContent().build();
    }

    /** 전체 리뷰 조회 **/
    @GetMapping
    public ResponseEntity<List<Response>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    /** 도서별 리뷰 조회 **/
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDto.Response>> getReviewsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }

    /** 유저별 리뷰 조회 **/
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto.Response>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }
}
