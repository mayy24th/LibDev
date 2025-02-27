package com.example.LibDev.recommendation.mapper;

import com.example.LibDev.recommendation.vo.UserBorrowActivityVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserActivityMapper {
    /** 사용자의 최다, 최신 대출도서의 주제와 저자 **/
    UserBorrowActivityVO findMostBorrowedTopicAndAuthor(@Param("userId") Long userId);

    /** 유저가 대출한 도서들의 도서번호 **/
    List<Long> findBorrowedBookIdsByUser(@Param("userId") Long userId);
}
