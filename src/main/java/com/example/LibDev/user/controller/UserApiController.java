package com.example.LibDev.user.controller;

import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.user.dto.JoinReqDto;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;

    @PostMapping("/api/v1/users")
    public ResponseEntity<UserResDto> join(@RequestBody JoinReqDto reqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.join(reqDto));
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<GlobalResponseDto> getUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, userService.info()));
    }


}