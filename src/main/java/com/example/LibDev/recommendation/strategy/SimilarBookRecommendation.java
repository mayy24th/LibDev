package com.example.LibDev.recommendation.strategy;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.RecommendationBookMapper;
import com.example.LibDev.recommendation.util.RecommendationUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimilarBookRecommendation implements RecommendationStrategy{

    private final RecommendationBookMapper recommendationBookMapper;
    private final BookRepository bookRepository;

    private static final int RECOMMENDATION_LIMIT = 5;

    @Override
    public List<RecommendationResponseDto> recommend(Long bookId, String email){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        List<Long> excludedBookIds = new ArrayList<>();
        excludedBookIds.add(bookId);

        List<RecommendationResponseDto> recommendations =
                recommendationBookMapper.findBooksByTopic(excludedBookIds, book.getTopicId());

        RecommendationUtils.fillWithBooksByAuthor(recommendations, excludedBookIds, book.getAuthor(), recommendationBookMapper, RECOMMENDATION_LIMIT);
        RecommendationUtils.fillWithLatestPublishedBooks(recommendations, excludedBookIds, recommendationBookMapper, RECOMMENDATION_LIMIT);

        return recommendations;
    }
}