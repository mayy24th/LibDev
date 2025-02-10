package com.example.LibDev.review.service;

import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.dto.ReviewDto.Response;
import com.example.LibDev.review.entity.Review;
import com.example.LibDev.review.mapper.ReviewMapper;
import com.example.LibDev.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    /** 리뷰 저장 **/
    @Transactional
    public void saveReview(ReviewDto.SaveRequest dto){
        // TODO : 유저, 도서 조회 로직 추가

        Review review = Review.builder()
                .content(dto.getContent())
                .userId(dto.getUserId())
                .bookId(dto.getBookId())
                .build();

        reviewRepository.save(review);
    }

    /** 리뷰 삭제 **/
    @Transactional
    // TODO : 유저 정보 파라미터 추가
    public void deleteReview(ReviewDto.DeleteRequest dto){
        // TODO : 유저 조회 로직 추가

        reviewRepository.deleteById(dto.getId());
    }

    /** 전체 리뷰 조회 **/
    public List<ReviewDto.Response> getAllReviews(){
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /** 도서별 리뷰 조회 **/
    public List<ReviewDto.Response> getReviewsByBook(Long bookId){
        // TODO : 도서 조회 로직 추가

        return reviewRepository.findByBookId(bookId).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /** 유저별 리뷰 조회 **/
    public List<ReviewDto.Response> getReviewsByUser(Long userId){
        // TODO : 유저 조회 로직 추가

        return reviewRepository.findByUserId(userId).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
}
