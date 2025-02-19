package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendationBookMapper {
    /** 도서 상세 페이지 해당 도서와 유사한 추천 (보고있는 도서는 제외) **/
    // 동일한 주제 (대출 횟수, 최신 출판)
    List<RecommendationResponseDto> findBooksByTopic(@Param("excludedBooks") List<Long> excludedBooks,
                                                     @Param("topicId") Integer topicId);
    // 동일한 저자 (대출 횟수, 최신 출판)
    List<RecommendationResponseDto> findBooksByAuthor(@Param("excludedBooks") List<Long> excludedBooks,
                                                      @Param("author") String author,
                                                      @Param("limit") int limit);
    // 주제, 저자와 상관없이 최신 출판 도서 추가
    List<RecommendationResponseDto> findLatestPublishedBooks(@Param("excludedBooks") List<Long> excludedBooks,
                                                             @Param("limit") int limit);

    /** 메인페이지 유저 대출내역 기반 도서 추천 **/
    List<RecommendationResponseDto> findUserBaseBooks(@Param("userId") Long userId,
                                                      @Param("topicId") Integer topicId);
}
