package com.example.LibDev.recommendation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import com.example.LibDev.recommendation.mapper.RecommendationMapper;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import com.example.LibDev.user.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBaseBookService {

    private final RecommendationMapper recommendationMapper;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private static final int RECOMMENDATION_LIMIT = 5;

    /**
     *  메인페이지에서 사용자 기반 도서 추천
     *  - 비로그인
     *  - popularBooks
     *
     *  - 로그인
     *  - 사용자의 대출내역이 존재하지 않는 처음에는 인기도서 (대출 수 기반)
     *  - 사용자의 대출내역이 존재하는 경우 대출횟수가 가장 높은 도서를 기반으로 조회
     */
    public List<RecommendationResponseDto> findUserBaseBooks(){

        // 비로그인
        String email = userService.getUserEmail();
        if(email == null){
            return recommendationMapper.findPopularBooks();
        }

        // 로그인
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 대출내역이 존재하지 않는 경우
        boolean hasBorrowHistory = borrowRepository.existsByUser(user);
        if (!hasBorrowHistory) {
            return recommendationMapper.findPopularBooks();
        }

        // 대출내역이 존재하는 경우
        Integer mostBorrowedTopic = recommendationMapper.findMostBorrowedTopic(user.getId());
        List<RecommendationResponseDto> recommendations = recommendationMapper.findUserBaseBooks(user.getId(), mostBorrowedTopic);
        if(recommendations.size() < RECOMMENDATION_LIMIT){
            List<Long> excludedBookIds = recommendations.stream()
                    .map(RecommendationResponseDto::getBookId)
                    .toList();

            List<RecommendationResponseDto> popularBooks = excludedBookIds.isEmpty()
                    ? recommendationMapper.findPopularBooks()
                    : recommendationMapper.findPopularBooksExcluding(excludedBookIds, RECOMMENDATION_LIMIT - recommendations.size());

            recommendations.addAll(popularBooks);
        }
        return recommendations;
    }
}
