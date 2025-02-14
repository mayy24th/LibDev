package com.example.LibDev.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/users/join")
    public String join() {
        return "user/join";
    }

    @GetMapping("/users/login")
    public String login() { return "user/login"; }

    @GetMapping("/users/mypage")
    public String mypage() { return "user/mypage"; }
}
