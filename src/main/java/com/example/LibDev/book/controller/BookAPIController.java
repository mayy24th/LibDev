package com.example.LibDev.book.controller;

import com.example.LibDev.book.dto.BookRequestDto;
import com.example.LibDev.book.dto.BookResponseDto;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @PostMapping(value = "/create", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createBook(@RequestParam("query") String query) {
        try {
            bookService.saveBookFromKakao(query);
            return ResponseEntity.ok("Books registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while registering books: " + e.getLocalizedMessage());
        }
    }

    // Kakao API에서 도서 검색 (DB에 저장 X, 검색 결과 반환)
    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDto>> searchBooks(@RequestParam String query) {
        List<BookResponseDto> books = bookService.searchBooksFromKakao(query);
        return ResponseEntity.ok(books);
    }

    // 도서 등록 API (청구기호 & 주제 ID 포함)
    @PostMapping("/register")
    public ResponseEntity<String> registerBook(@RequestBody BookRequestDto bookRequestDto) {
        bookService.registerBook(bookRequestDto);
        return ResponseEntity.ok("도서 등록 성공");
    }

    // 국립중앙도서관 API에서 청구기호와 주제 ID 가져오는 API
    @GetMapping("/library-info")
    public ResponseEntity<Map<String, String>> getLibraryInfo(@RequestParam String isbn) {
        Map<String, String> libraryInfo = bookService.fetchLibraryData(isbn);
        return ResponseEntity.ok(libraryInfo);
    }



}
