package com.example.LibDev.borrow.controller;

import com.example.LibDev.borrow.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BorrowAPIController {
    private final BorrowService borrowService;

    @PostMapping("/api/v1/borrow")
    public ResponseEntity<?> borrow(@RequestParam Long bookId) {
        borrowService.borrow(bookId);
        return ResponseEntity.ok().build();
    }

}
