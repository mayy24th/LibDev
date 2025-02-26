package com.example.LibDev.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** 메인 페이지 **/
    @GetMapping("/home")
    public String home() {
        return "home/home";
    }

    /** 이용 안내 **/
    @GetMapping("/guide")
    public String libraryGuide() {
        return "home/library-guide";
    }

    /** 인사말 **/
    @GetMapping("/greetings")
    public String libraryGreetings() {
        return "home/library-greetings";
    }

    /** 연혁 **/
    @GetMapping("/history")
    public String libraryHistory(){
        return "home/history";
    }
}
