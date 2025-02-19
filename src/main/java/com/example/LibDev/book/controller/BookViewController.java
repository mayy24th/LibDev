package com.example.LibDev.book.controller;

import com.example.LibDev.book.dto.BookResponseDto;
import com.example.LibDev.book.dto.KakaoBookResponseDto;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookViewController {
    private final BookService bookService;

    public BookViewController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/register")
    public String showBookRegisterPage() {
        return "book/register";
    }

    @GetMapping("/search-simple")
    public String showBookListPage() {
        return "book/searchSimple";
    }

    @GetMapping("/{bookId}")
    public String showBookDetailPage(@PathVariable Long bookId, Model model) {
        Book book = bookService.findBookById(bookId);
        model.addAttribute("book", book);
        return "book/detail";
    }

    // 주제 선택 페이지
    @GetMapping("/search-topic")
    public String showTopicSelectionPage(Model model) {
        return "book/searchTopic";
    }

    // 주제별 도서 목록 페이지
    @GetMapping("/search-topic/{topicId}")
    public String showBooksByTopic(@PathVariable int topicId, Model model) {
        List<BookResponseDto> books = bookService.findBooksByTopic(topicId); // 주제별 도서 목록 조회
        model.addAttribute("books", books);
        return "book/searchTopicList";
    }

}
