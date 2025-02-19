package com.example.LibDev.recommendation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.RecommendationBookMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookSimilarityService {

    private final RecommendationBookMapper recommendationBookMapper;
    private final BookRepository bookRepository;

    private static final int RECOMMENDATION_LIMIT = 5;

    /** 도서 상세페이지에서 해당 도서와 유사한 도서 추천 **/
    public List<RecommendationResponseDto> findSimilarBooks(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        List<Long> excludedBookIds = new ArrayList<>();
        excludedBookIds.add(bookId);

        List<RecommendationResponseDto> recommendations = recommendationBookMapper.findBooksByTopic(excludedBookIds, book.getTopicId());
        fillWithBooksByAuthor(recommendations, excludedBookIds, book.getAuthor());
        fillWithLatestPublishedBooks(recommendations, excludedBookIds);
        return recommendations;
    }

    private void fillWithLatestPublishedBooks(List<RecommendationResponseDto> recommendations, List<Long> excludedBookIds){
        if (recommendations.size() < RECOMMENDATION_LIMIT) {
            excludedBookIds.addAll(recommendations.stream()
                    .map(RecommendationResponseDto::getBookId)
                    .toList());

            List<RecommendationResponseDto> latestPublishedBooks = recommendationBookMapper.findLatestPublishedBooks(excludedBookIds, RECOMMENDATION_LIMIT - recommendations.size());

            recommendations.addAll(latestPublishedBooks);
        }
    }

    private void fillWithBooksByAuthor(List<RecommendationResponseDto> recommendations, List<Long> excludedBookIds, String author) {
        if (recommendations.size() < RECOMMENDATION_LIMIT) {
            excludedBookIds.addAll(recommendations.stream()
                    .map(RecommendationResponseDto::getBookId)
                    .toList());

            List<RecommendationResponseDto> booksByAuthor = recommendationBookMapper.findBooksByAuthor(excludedBookIds, author, RECOMMENDATION_LIMIT - recommendations.size());

            recommendations.addAll(booksByAuthor);
        }
    }
}
