package com.example.LibDev.review.entity;

import com.example.LibDev.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "review")
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO : 유저<->리뷰 연관관계 매핑 추가
    @Column(name = "user_id")
    private Long userId;

    // TODO : 도서<->리뷰 연관관계 매핑 추가
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "content", length = 50)
    private String content;
}