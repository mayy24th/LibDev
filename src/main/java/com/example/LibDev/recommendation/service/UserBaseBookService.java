package com.example.LibDev.recommendation.service;

import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.PopularBookMapper;
import com.example.LibDev.recommendation.mapper.RecommendationBookMapper;
import com.example.LibDev.recommendation.mapper.UserActivityMapper;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import com.example.LibDev.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBaseBookService {

    private final RecommendationBookMapper recommendationBookMapper;
    private final PopularBookMapper popularBookMapper;
    private final UserActivityMapper userActivityMapper;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private static final int RECOMMENDATION_LIMIT = 5;

    /** 메인페이지에서 유저 기반 도서 추천 **/
    public List<RecommendationResponseDto> findUserBaseBooks(){
        // 비로그인 - popularBooks
        String email = userService.getUserEmail();
        if(email == null){
            return popularBookMapper.findPopularBooks(null, RECOMMENDATION_LIMIT);
        }

        //로그인
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 대출내역이 존재하지 않는 경우 경우 - 인기도서 (대출 수 기반)
        if (!borrowRepository.existsByUser(user)) {
            return popularBookMapper.findPopularBooks(null, RECOMMENDATION_LIMIT);
        }

        // 대출내역이 존재하는 경우 - 최다, 최신 대출 도서를 기반으로 조회 -> 부족한 경우 이미 추천한 도서를 제외하고 인기도서로 데이터 추가
        Integer mostBorrowedTopic = userActivityMapper.findMostBorrowedTopic(user.getId());
        List<RecommendationResponseDto> recommendations = recommendationBookMapper.findUserBaseBooks(user.getId(), mostBorrowedTopic);
        fillWithPopularBooks(recommendations);

        return recommendations;
    }

    private void fillWithPopularBooks(List<RecommendationResponseDto> recommendations) {
        List<Long> excludedBookIds = null;

        // 이미 추천된 도서가 있으면 제외할 도서번호 목록에 추가
        if (!recommendations.isEmpty()) {
            excludedBookIds = recommendations.stream()
                    .map(RecommendationResponseDto::getBookId)
                    .toList();
        }

        if (recommendations.size() < RECOMMENDATION_LIMIT) {
            List<RecommendationResponseDto> popularBooks = popularBookMapper.findPopularBooks(excludedBookIds, RECOMMENDATION_LIMIT - recommendations.size());
            recommendations.addAll(popularBooks);
        }
    }
}
