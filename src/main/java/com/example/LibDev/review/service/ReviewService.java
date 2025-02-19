package com.example.LibDev.review.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.review.dto.ReviewDto;
import com.example.LibDev.review.entity.Review;
import com.example.LibDev.review.mapper.ReviewMapper;
import com.example.LibDev.review.repository.ReviewRepository;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import com.example.LibDev.user.service.UserService;
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
    private final UserService userService;

    /** 한줄평 저장 **/
    @Transactional
    public void saveReview(ReviewDto.SaveRequest dto){
        UserResDto userResDto = userService.info();
        if(userResDto == null){
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        User user = userRepository.findByEmail(userResDto.getEmail())
                .orElseThrow(()-> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(()-> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        Review review = Review.builder()
                .content(dto.getContent())
                .user(user)
                .book(book)
                .build();

        reviewRepository.save(review);
    }

    /** 한줄평 삭제 **/
    @Transactional
    public void deleteReview(Long reviewId){
        UserResDto userResDto = userService.info();
        if(userResDto == null){
            throw new CustomException(CustomErrorCode.AUTHENTICATION_REQUIRED);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new CustomException(CustomErrorCode.REVIEW_NOT_FOUND));

        // 본인이 작성한 한줄평인지 확인
        if(!review.getUser().getEmail().equals(userResDto.getEmail())){
            throw new CustomException(CustomErrorCode.REVIEW_DELETE_FORBIDDEN);
        }

        reviewRepository.delete(review);
    }

    /** 한줄평 수정 **/
    @Transactional
    public void updateReview(ReviewDto.UpdateRequest dto, Long reviewId){
        UserResDto userResDto = userService.info();
        if(userResDto == null){
            throw new CustomException(CustomErrorCode.AUTHENTICATION_REQUIRED);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new CustomException(CustomErrorCode.REVIEW_NOT_FOUND));

        // 본인이 작성한 한줄평인지 확인
        if(!review.getUser().getEmail().equals(userResDto.getEmail())){
            throw new CustomException(CustomErrorCode.REVIEW_DELETE_FORBIDDEN);
        }

        review.updateContent(dto.getContent());
    }


    /** 전체 한줄평 조회 **/
    public List<ReviewDto.Response> getAllReviews() {
        String email = userService.getUserEmail();

        List<Review> reviews = reviewRepository.findAll();
        return reviewMapper.toDtoList(reviews, email);
    }

    /** 도서별 한줄평 조회 **/
    public List<ReviewDto.Response> getReviewsByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        String email = userService.getUserEmail();

        List<Review> reviews = reviewRepository.findByBook(book);
        return reviewMapper.toDtoList(reviews, email);
    }

    /** 유저별 한줄평 조회 **/
    public List<ReviewDto.Response> getReviewsByUser(){
        UserResDto userResDto = userService.info();
        if(userResDto == null){
            throw new CustomException(CustomErrorCode.AUTHENTICATION_REQUIRED);
        }

        User user = userRepository.findByEmail(userResDto.getEmail())
                .orElseThrow(()-> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByUser(user);
        return reviewMapper.toDtoList(reviews, userResDto.getEmail());
    }
}
