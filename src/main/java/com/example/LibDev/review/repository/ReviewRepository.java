package com.example.LibDev.review.repository;

import com.example.LibDev.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** 전체 리뷰 조회 **/
    List<Review> findAll();

    /** 도서별 리뷰 조회 **/
    List<Review> findByBookId(Long bookId);

    /** 유저별 리뷰 조회 **/
    List<Review> findByUserId(Long userId);
}
