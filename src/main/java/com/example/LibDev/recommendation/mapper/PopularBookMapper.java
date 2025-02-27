package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.vo.RecommendedBookVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PopularBookMapper {
    /** 인기자료 페이지 인기도서 추천 **/
    /** 메인페이지, 추천자료페이지에서 비로그인, 대출정보 없는 유저는 인기 도서 추천 **/
    List<RecommendedBookVO> findPopularBooks(@Param("limit") int limit);
}
