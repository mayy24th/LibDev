package com.example.LibDev.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    @GetMapping("/admin/management")
    public String mypage() {
        return "admin/admin-management";
    }

    @GetMapping("/admin/user-management")
    public String usermanagement() {
        return "admin/admin-user-management";
    }

}
