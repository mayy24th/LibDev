package com.example.LibDev.book.repository;

import com.example.LibDev.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String title);
    List<Book> findByAuthorContaining(String author);
    List<Book> findByPublisherContaining(String publisher);
    List<Book> findByTitleContainingOrAuthorContainingOrPublisherContaining(String title, String author, String publisher);

    Optional<Book> findByCallNumber(String callNumber);
}