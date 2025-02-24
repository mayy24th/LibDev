package com.example.LibDev.book.controller;

import com.example.LibDev.book.dto.BookRequestDto;
import com.example.LibDev.book.dto.BookResponseDto;
import com.example.LibDev.book.dto.KakaoBookResponseDto;
import com.example.LibDev.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<KakaoBookResponseDto>> searchKakaoBooks(@RequestParam String query) {
        List<KakaoBookResponseDto> books = bookService.searchBooksToRegister(query);

        // 저자 또는 출판사가 빈칸이면 제외
        List<KakaoBookResponseDto> filteredBooks = books.stream()
                .filter(book -> book.getAuthor() != null && !book.getAuthor().trim().isEmpty())
                .filter(book -> book.getPublisher() != null && !book.getPublisher().trim().isEmpty())
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredBooks);
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

    // 도서 목록 조회 API
    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getBooks(
            @RequestParam(required = false, value = "query") String query,
            @RequestParam(value = "searchType", defaultValue = "전체") String searchType) {

        List<BookResponseDto> books;

        if ("전체".equals(searchType)) {
            books = bookService.searchBooks(query); // 검색어로 도서 전체 조회
        } else if ("제목".equals(searchType)) {
            books = bookService.searchByTitle(query); // 제목으로만 검색
        } else if ("저자".equals(searchType)) {
            books = bookService.searchByAuthor(query); // 저자별로 검색
        } else if ("출판사".equals(searchType)) {
            books = bookService.searchByPublisher(query); // 출판사별로 검색
        } else {
            books = bookService.searchBooks(query); // 기본적으로 전체 검색 처리
        }

        return ResponseEntity.ok(books);
    }

    // 주제별 도서 목록 조회 API
    @GetMapping("/search-topic/{topicId}")
    public ResponseEntity<List<BookResponseDto>> searchBooksByTopic(@PathVariable int topicId) {
        List<BookResponseDto> books = bookService.findBooksByTopic(topicId);
        return ResponseEntity.ok(books);
    }

    // 도서 삭제 API
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        boolean isDeleted = bookService.deleteBook(bookId);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 개별 도서 정보 조회 ( 대출상태, 반납예정일, 예약가능 여부 )
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBookDetails(@PathVariable Long bookId) {
        BookResponseDto bookResponseDto = bookService.getBookDetails(bookId);
        return ResponseEntity.ok(bookResponseDto);
    }
}
