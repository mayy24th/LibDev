package com.example.LibDev.book.controller;

import com.example.LibDev.book.dto.BookRequestDto;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookAPIController {
    private final BookService bookService;

    /*
    // 도서 직접 등록
    @PostMapping("/create")
    public ResponseEntity<Book> createBook(@RequestBody BookRequestDto bookRequestDto) {
        Book savedBook = bookService.saveBook(bookRequestDto);
        return ResponseEntity.ok(savedBook);
    }*/

    // Kakao API에서 도서 정보를 검색해 DB에 등록하는 API
    @PostMapping(value = "/register", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> registerBook(@RequestParam("query") String query) {
        try {
            bookService.saveBookFromKakao(query);
            return ResponseEntity.ok("Books registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while registering books: " + e.getLocalizedMessage());
        }
    }
}
