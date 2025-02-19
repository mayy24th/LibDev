package com.example.LibDev.recommendation.controller;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.service.BookSimilarityService;
import com.example.LibDev.recommendation.service.UserBaseBookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final BookSimilarityService bookSimilarityService;
    private final UserBaseBookService userBaseBookService;

    /** 도서 상세페이지에서 해당 도서와 유사한 도서 추천 **/
    @GetMapping("/similar/{bookId}")
    public ResponseEntity<List<RecommendationResponseDto>> recommendSimilarBooks(@PathVariable Long bookId){
        List<RecommendationResponseDto> recommendedBooks = bookSimilarityService.findSimilarBooks(bookId);
        return ResponseEntity.ok(recommendedBooks);
    }

    /** 메인페이지에서 사용자 기반 도서 추천 **/
    @GetMapping("/user")
    public ResponseEntity<List<RecommendationResponseDto>> recommendUserBaseBooks(){
        List<RecommendationResponseDto> recommendedBooks = userBaseBookService.findUserBaseBooks();
        return ResponseEntity.ok(recommendedBooks);
    }
}
