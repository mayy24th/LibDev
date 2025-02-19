package com.example.LibDev.recommendation.service;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.strategy.SimilarBookRecommendation;
import com.example.LibDev.recommendation.strategy.UserBaseRecommendation;
import com.example.LibDev.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final SimilarBookRecommendation similarBookRecommendation;
    private final UserBaseRecommendation userBaseRecommendation;
    private final UserService userService;

    /**
     * 도서 ID를 기준으로 유사한 도서를 추천
     * @param bookId 기준 도서 ID
     * @return 추천 도서 리스트
     */
    public List<RecommendationResponseDto> recommendSimilarBooks(Long bookId) {
        return similarBookRecommendation.recommend(bookId, null);
    }

    /**
     * 사용자 Email을 기준으로 개인화된 도서를 추천
     * @return 추천 도서 리스트
     */
    public List<RecommendationResponseDto> recommendUserBaseBooks() {
        String email = userService.getUserEmail();
        return userBaseRecommendation.recommend(null, email);
    }
}
