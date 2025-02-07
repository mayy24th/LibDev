package com.example.LibDev.auth.controller;

import com.example.LibDev.auth.dto.LoginReqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiController {

    @PostMapping("/api/v1/auths/login")
    public ResponseEntity<Void> login(@RequestBody LoginReqDto loginReqDto){ return null; }
}
