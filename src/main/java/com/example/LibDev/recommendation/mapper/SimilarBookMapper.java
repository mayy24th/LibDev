package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.vo.RecommendedBookVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SimilarBookMapper {
    /** 도서 상세 페이지 해당 도서와 유사한 추천 (보고있는 도서는 제외) **/
    List<RecommendedBookVO> findSimilarBooks(@Param("bookId") Long bookId,
                                             @Param("topicId") Integer topicId,
                                             @Param("author") String author,
                                             @Param("limit") int limit);
}
