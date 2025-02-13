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

    @Column(unique = true, length = 50)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true; // 대출가능여부

    @Column(name = "call_number", length = 50)
    private String callNumber; // 청구기호

    public void updateIsAvailable(Boolean isAvailable) {this.isAvailable = isAvailable;}

}
