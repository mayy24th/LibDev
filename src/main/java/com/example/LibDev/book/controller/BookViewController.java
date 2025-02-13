package com.example.LibDev.book.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/books")
public class BookViewController {
    @GetMapping("/register")
    public String showBookRegisterPage() {
        return "book/register"; // src/main/resources/templates/book/register.html
    }
}
