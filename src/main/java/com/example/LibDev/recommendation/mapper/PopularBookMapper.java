package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.dto.RecommendationResponseDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PopularBookMapper {
    /** 메인페이지 비로그인, 대출정보 없는 유저 도서 추천 **/
    /** 대출내역 기반으로 추천 후에 인기도서로 부족한 도서데이터 추천 **/
    List<RecommendationResponseDto> findPopularBooks(@Param("excludedBooks") List<Long> excludedBooks,
                                                     @Param("limit") int limit);
}
