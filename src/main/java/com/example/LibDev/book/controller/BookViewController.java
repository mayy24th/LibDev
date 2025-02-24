package com.example.LibDev.book.controller;

import com.example.LibDev.book.dto.BookResponseDto;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "book/search-simple";
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
        return "book/search-topic";
    }

    // 주제별 도서 목록 페이지
    @GetMapping("/search-topic/{topicId}")
    public String showBooksByTopic(@PathVariable int topicId, Model model) {
        List<BookResponseDto> books = bookService.findBooksByTopic(topicId);
        model.addAttribute("books", books);
        return "book/search-topic-list";
    }

    // 도서 관리 페이지
    @GetMapping("/book-admin")
    public String showAdminBookListPage() {
        return "book/book-admin";
    }

    // 신착자료 페이지
    @GetMapping("/new-books")
    public String showNewBooksPage() {
        return "book/new-books";
    }

}
