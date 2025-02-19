package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendationMapper {
    /** 도서 상세 페이지 유사 도서 추천 **/
    List<RecommendationResponseDto> findSimilarBooks(@Param("bookId") Long bookId,
                                                     @Param("topicId") Integer topicId,
                                                     @Param("author") String author);

    /** 메인페이지 비로그인, 대출정보 없는 유저 도서 추천 **/
    List<RecommendationResponseDto> findPopularBooks();

    /** 메인페이지 유저 대출내역 기반 도서 추천 **/
    Integer findMostBorrowedTopic(@Param("userId") Long userId);
    List<RecommendationResponseDto> findUserBaseBooks(@Param("userId") Long userId,
                                                      @Param("topicId") Integer topicId);
    List<RecommendationResponseDto> findPopularBooksExcluding(@Param("excludedBooks") List<Long> excludedBooks,
                                                              @Param("limit") int limit);

}
