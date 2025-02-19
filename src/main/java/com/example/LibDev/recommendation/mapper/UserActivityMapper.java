package com.example.LibDev.recommendation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserActivityMapper {
    /** 사용자의 최다, 최신 대출도서의 주제 **/
    Integer findMostBorrowedTopic(@Param("userId") Long userId);
}
