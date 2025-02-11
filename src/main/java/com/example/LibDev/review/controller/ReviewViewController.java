package com.example.LibDev.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/review")
public class ReviewViewController {

    /** 전체 리뷰 조회 **/
    @GetMapping("/list")
    public String getAllReviews() {
        return "review/list";
    }

    /** 리뷰 저장 **/
    @GetMapping
    public String saveReview(){
        return "review/form";
    }
}
