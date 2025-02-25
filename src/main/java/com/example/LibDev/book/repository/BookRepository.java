package com.example.LibDev.book.repository;

import com.example.LibDev.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingOrderByCreatedAtDesc(String title);
    List<Book> findByAuthorContainingOrderByCreatedAtDesc(String author);
    List<Book> findByPublisherContainingOrderByCreatedAtDesc(String publisher);
    List<Book> findByTitleContainingOrAuthorContainingOrPublisherContainingOrderByCreatedAtDesc(String title, String author, String publisher);
    List<Book> findAllByOrderByCreatedAtDesc();

    List<Book> findByCallNumberStartingWith(String callNumber);
    List<Book> findByTopicIdOrderByCreatedAtDesc(int topicId);
    List<Book> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
}