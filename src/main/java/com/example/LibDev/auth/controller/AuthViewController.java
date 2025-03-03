package com.example.LibDev.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/auths/password-find")
    public String passwordFind() {
        return "user/password-find";
    }

    @GetMapping("/auths/password-find/codeVerify")
    public String passwordFindCodeVerify() {
        return "user/password-find-code-input";
    }

    @GetMapping("/auths/password-find/reset")
    public String passwordFindReset() {
        return "user/password-find-reset";
    }



}
