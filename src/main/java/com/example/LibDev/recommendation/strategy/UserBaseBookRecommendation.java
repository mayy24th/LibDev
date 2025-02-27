package com.example.LibDev.recommendation.strategy;

import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.PopularBookMapper;
import com.example.LibDev.recommendation.mapper.UserActivityMapper;
import com.example.LibDev.recommendation.mapper.UserBaseBookMapper;
import com.example.LibDev.recommendation.vo.RecommendedBookVO;
import com.example.LibDev.recommendation.vo.UserBorrowActivityVO;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBaseBookRecommendation implements RecommendationStrategy {

    private final PopularBookMapper popularBookMapper;
    private final UserActivityMapper userActivityMapper;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final UserBaseBookMapper userBaseBookMapper;

    private static final int RECOMMENDATION_LIMIT = 5;

    @Override
    public List<RecommendationResponseDto> recommend(Long bookId, String email) {
        // 비로그인 - 인기 도서 추천
        if (email == null) {
            List<RecommendedBookVO> books = popularBookMapper.findPopularBooks(RECOMMENDATION_LIMIT);
            return books.stream()
                    .map(RecommendedBookVO::toDto)
                    .toList();
        }

        // 로그인된 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 대출 내역이 없는 경우 - 인기 도서 추천
        if (!borrowRepository.existsByUser(user)) {
            List<RecommendedBookVO> books =  popularBookMapper.findPopularBooks(RECOMMENDATION_LIMIT);
            return books.stream()
                    .map(RecommendedBookVO::toDto)
                    .toList();
        }

        // 대출 내역이 있는 경우 - 최다 대출 도서 중 가장 최신 대출 도서를 기반으로 추천
        UserBorrowActivityVO userBorrowInfo = userActivityMapper.findMostBorrowedTopicAndAuthor(user.getId());
        List<Long> excludedBookIds = userActivityMapper.findBorrowedBookIdsByUser(user.getId());
        List<RecommendedBookVO> books = userBaseBookMapper.findUserBaseBooks(user.getId(), userBorrowInfo.getTopicId(), userBorrowInfo.getAuthor(), excludedBookIds, RECOMMENDATION_LIMIT);

        return books.stream()
                .map(RecommendedBookVO::toDto)
                .toList();
    }
}