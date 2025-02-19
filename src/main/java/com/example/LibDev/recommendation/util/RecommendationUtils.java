package com.example.LibDev.recommendation.util;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.PopularBookMapper;
import com.example.LibDev.recommendation.mapper.RecommendationBookMapper;
import com.example.LibDev.recommendation.vo.RecommendedBookVO;
import java.util.List;

public class RecommendationUtils {

    public static void fillWithLatestPublishedBooks(List<RecommendedBookVO> recommendations,
                                                    List<Long> excludedBookIds,
                                                    RecommendationBookMapper mapper,
                                                    int limit) {
        if (recommendations.size() < limit) {
            excludedBookIds.addAll(recommendations.stream()
                    .map(RecommendedBookVO::getBookId)
                    .toList());

            List<RecommendedBookVO> latestPublishedBooks = mapper.findLatestPublishedBooks(excludedBookIds, limit - recommendations.size());
            recommendations.addAll(latestPublishedBooks);
        }
    }

    public static void fillWithBooksByAuthor(List<RecommendedBookVO> recommendations,
                                             List<Long> excludedBookIds,
                                             String author,
                                             RecommendationBookMapper mapper,
                                             int limit) {
        if (recommendations.size() < limit) {
            excludedBookIds.addAll(recommendations.stream()
                    .map(RecommendedBookVO::getBookId)
                    .toList());

            List<RecommendedBookVO> booksByAuthor = mapper.findBooksByAuthor(excludedBookIds, author, limit - recommendations.size());
            recommendations.addAll(booksByAuthor);
        }
    }

    public static void fillWithPopularBooks(List<RecommendedBookVO> recommendations,
                                            PopularBookMapper mapper,
                                            int limit) {
        // 이미 추천된 도서가 있으면 제외할 도서번호 목록에 추가
        List<Long> excludedBookIds = recommendations.stream()
                .map(RecommendedBookVO::getBookId)
                .toList();

        if (recommendations.size() < limit) {
            List<RecommendedBookVO> popularBooks = mapper.findPopularBooks(excludedBookIds, limit - recommendations.size());
            recommendations.addAll(popularBooks);
        }
    }
}
