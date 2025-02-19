package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendationBookMapper {
    /** 도서 상세 페이지 유사 도서 추천 **/
    List<RecommendationResponseDto> findSimilarBooks(@Param("bookId") Long bookId,
                                                     @Param("topicId") Integer topicId,
                                                     @Param("author") String author);

    /** 메인페이지 유저 대출내역 기반 도서 추천 **/
    List<RecommendationResponseDto> findUserBaseBooks(@Param("userId") Long userId,
                                                      @Param("topicId") Integer topicId);
}
