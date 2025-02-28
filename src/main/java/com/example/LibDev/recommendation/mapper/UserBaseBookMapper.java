package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.vo.RecommendedBookVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserBaseBookMapper {
    /** 메인페이지, 추천자료페이지 유저 대출내역 기반 도서 추천 **/
    List<RecommendedBookVO> findUserBaseBooks(@Param("userId") Long userId,
                                              @Param("topicId") Integer topicId,
                                              @Param("author") String author,
                                              @Param("excludedBookIds") List<Long> excludedBookIds,
                                              @Param("limit") int limit);
}
