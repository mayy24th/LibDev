package com.example.LibDev.recommendation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.RecommendationMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookSimilarityService {

    private final RecommendationMapper recommendationMapper;
    private final BookRepository bookRepository;

    /** 도서 상세페이지에서 해당 도서와 유사한 도서 추천 **/
    public List<RecommendationResponseDto> findSimilarBooks(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new IllegalArgumentException("도서 정보를 찾을 수 없습니다."));

        return recommendationMapper.findSimilarBooks(bookId, book.getTopicId(), book.getAuthor());
    }
}
