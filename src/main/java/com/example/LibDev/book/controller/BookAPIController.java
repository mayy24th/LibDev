package com.example.LibDev.book.controller;

import com.example.LibDev.book.dto.BookRequestDto;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookAPIController {
    private final BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<Book> createBook(@RequestBody BookRequestDto bookRequestDto) {
        Book savedBook = bookService.saveBook(bookRequestDto);
        return ResponseEntity.ok(savedBook);
    }
}
