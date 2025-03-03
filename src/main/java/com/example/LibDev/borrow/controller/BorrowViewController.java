package com.example.LibDev.borrow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/borrows")
public class BorrowViewController {
    /* 회원 대출 현황 조회 페이지 */
    @GetMapping("/my-status")
    public String myBorrowStatus(){ return "borrow/my-status"; }

    /* 회원 대출 이력 조회 페이지 */
    @GetMapping("/my-history")
    public String myBorrowHistory(){ return "borrow/my-history"; }

    /* 전체 대출 조회 페이지(관리자) */
    @GetMapping("/admin")
    public String borrowList(){ return "borrow/admin-borrow-manage"; }
}
