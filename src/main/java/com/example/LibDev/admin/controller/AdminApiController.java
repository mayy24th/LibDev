package com.example.LibDev.admin.controller;

import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminApiController {

    private final UserService userService;

    @GetMapping("/api/admin/v1/admins")
    public ResponseEntity<GlobalResponseDto> getAdmins() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, userService.info()));
    }
}
