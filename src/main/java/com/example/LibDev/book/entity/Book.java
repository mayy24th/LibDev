package com.example.LibDev.book.entity;

import com.example.LibDev.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book")
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(name = "topic_id", nullable = false)
    private Integer topicId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false, length = 100)
    private String publisher;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true; // 대출가능여부

    @Column(name = "call_number", length = 50)
    private String callNumber; // 청구기호

    @Column(name = "thumbnail", length = 500)
    private String thumbnail; // 도서 표지 (썸네일 URL)

    public void updateIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

}
