package com.example.LibDev.review.repository;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.review.entity.Review;
import com.example.LibDev.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** 전체 한줄평 조회 **/
    List<Review> findAll();

    /** 도서별 한줄평 조회 **/
    List<Review> findByBook(Book book);

    /** 유저별 한줄평 조회 **/
    List<Review> findByUser(User user);
}
