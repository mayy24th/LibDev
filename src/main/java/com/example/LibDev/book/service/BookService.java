package com.example.LibDev.book.service;

import com.example.LibDev.book.dto.BookRequestDto;
import com.example.LibDev.book.dto.BookResponseDto;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    @Transactional
    public Book saveBook(BookRequestDto bookRequestDto) {
        Book book = bookRequestDto.toEntity();
        return bookRepository.save(book);
    }
}
