package com.example.LibDev.borrow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BorrowViewController {
    @GetMapping("/my/borrow-list")
    public String borrowList(){ return "borrow/my-status"; }
}
