package com.example.LibDev.book.repository;

import com.example.LibDev.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}