package com.example.LibDev.review.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.dto.ReviewDto.Response;
import com.example.LibDev.review.entity.Review;
import com.example.LibDev.review.mapper.ReviewMapper;
import com.example.LibDev.review.repository.ReviewRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /** 한줄평 저장 **/
    @Transactional
    public void saveReview(ReviewDto.SaveRequest dto){
        // TODO : 1L -> dto.getUserId(), dto.getBookId()로 수정
        User user = userRepository.findById(1L)
                .orElseThrow(()-> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        Book book = bookRepository.findById(1L)
                .orElseThrow(()-> new IllegalArgumentException("도서 정보를 찾을 수 없습니다."));

        Review review = Review.builder()
                .content(dto.getContent())
                .user(user)
                .book(book)
                .build();

        reviewRepository.save(review);
    }

    /** 한줄평 삭제 **/
    @Transactional
    public void deleteReview(ReviewDto.DeleteRequest dto, Long userId){
        Review review = reviewRepository.findById(dto.getId())
                .orElseThrow(()-> new IllegalArgumentException("한줄평 정보를 찾을 수 없습니다."));

        // 본인이 작성한 한줄평인지 확인
        if(!review.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인이 작성한 한줄평만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    /** 한줄평 수정 **/
    @Transactional
    public void updateReview(ReviewDto.UpdateRequest dto, Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new IllegalArgumentException("한줄평 정보를 찾을 수 없습니다."));

        // TODO : 본인이 작성한 한줄평인지 확인하는 로직 추가

        review.updateContent(dto.getContent());
        review.setUpdatedAt();
    }


    /** 전체 한줄평 조회 **/
    public List<ReviewDto.Response> getAllReviews(){
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /** 도서별 한줄평 조회 **/
    public List<ReviewDto.Response> getReviewsByBook(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new IllegalArgumentException("도서 정보를 찾을 수 없습니다."));

        return reviewRepository.findByBook(book).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /** 유저별 한줄평 조회 **/
    public List<ReviewDto.Response> getReviewsByUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        return reviewRepository.findByUser(user).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
}
