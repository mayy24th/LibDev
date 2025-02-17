package com.example.LibDev.review.controller;

import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.dto.ReviewDto.Response;
import com.example.LibDev.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewAPIController {
    private final ReviewService reviewService;

    /** 한줄평 저장 **/
    @PostMapping
    public ResponseEntity<Void> saveReview(@Valid @RequestBody ReviewDto.SaveRequest saveRequestDto){
        reviewService.saveReview(saveRequestDto);

        return ResponseEntity.ok().build();
    }

    /** 한줄평 삭제 **/
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId){
        reviewService.deleteReview(reviewId);

        return ResponseEntity.noContent().build();
    }

    /** 한줄평 수정 **/
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,
                                             @Valid @RequestBody ReviewDto.UpdateRequest updateRequestDto){
        reviewService.updateReview(updateRequestDto, reviewId);

        return ResponseEntity.ok().build();
    }

    /** 전체 한줄평 조회 **/
    @GetMapping
    public ResponseEntity<List<Response>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    /** 도서별 한줄평 조회 **/
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDto.Response>> getReviewsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }

    /** 유저별 한줄평 조회 **/
    @GetMapping("/user")
    public ResponseEntity<List<ReviewDto.Response>> getReviewsByUser() {
        return ResponseEntity.ok(reviewService.getReviewsByUser());
    }
}
