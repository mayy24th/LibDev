package com.example.LibDev.borrow.controller;

import com.example.LibDev.borrow.dto.BorrowResDto;
import com.example.LibDev.borrow.dto.ExtendResDto;
import com.example.LibDev.borrow.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BorrowAPIController {
    private final BorrowService borrowService;

    /* 회원별 대출 현황 조회 */
    @GetMapping("/api/v1/my/borrow-status")
    public ResponseEntity<List<BorrowResDto>> getBorrowStatus() {
        return ResponseEntity.ok(borrowService.getCurrentBorrowsByUser());
    }

    /* 대출 생성 */
    @PostMapping("/api/v1/borrow")
    public ResponseEntity<?> borrow(@RequestParam Long bookId) {
        borrowService.borrow(bookId);
        return ResponseEntity.ok().build();
    }

    /* 대출 연장 */
    @PatchMapping("/api/v1/extend/{borrowId}")
    public ResponseEntity<ExtendResDto> extend(@PathVariable Long borrowId) {
        return ResponseEntity.ok(borrowService.extendReturnDate(borrowId));
    }
}
