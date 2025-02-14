package com.example.LibDev.book.controller;

import com.example.LibDev.book.dto.BookResponseDto;
import com.example.LibDev.book.dto.KakaoBookResponseDto;
import com.example.LibDev.book.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("search-simple")
    public String showBookListPage() {
        return "book/searchSimple";
    }

}
