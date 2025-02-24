package com.example.LibDev.recommendation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommendation")
public class RecommendationViewController {

    /** 인기 도서, 추천 도서 **/
    @GetMapping({"/popular", "list"})
    public String popularBooks() {
        return "recommendation/list";
    }
}
