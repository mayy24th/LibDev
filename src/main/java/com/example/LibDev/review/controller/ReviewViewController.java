package com.example.LibDev.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/review")
public class ReviewViewController {

    /** 전체 한줄평 조회 **/
    @GetMapping({"/list", "/user", "/book/{bookId}"})
    public String getAllReviews() {
        return "review/list";
    }
}
