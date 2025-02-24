package com.example.LibDev.recommendation.strategy;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.PopularBookMapper;
import com.example.LibDev.recommendation.vo.RecommendedBookVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularBookRecommendation implements RecommendationStrategy {

    private final PopularBookMapper popularBookMapper;

    private static final int RECOMMENDATION_LIMIT = 5;

    @Override
    public List<RecommendationResponseDto> recommend(Long bookId, String email){
        List<RecommendedBookVO> books = popularBookMapper.findPopularBooks(null, RECOMMENDATION_LIMIT);

        return books.stream()
                .map(RecommendedBookVO::toDto)
                .toList();
    }
}
