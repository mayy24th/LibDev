package com.example.LibDev.user.controller;

import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.user.dto.JoinReqDto;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.dto.UserUpdateReqDto;
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

    @PatchMapping("/api/v1/users")
    public ResponseEntity<GlobalResponseDto> updateUser(@RequestBody UserUpdateReqDto userUpdateReqDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, userService.update(userUpdateReqDto)));
    }

    @PatchMapping("/api/v1/users/password")
    public ResponseEntity<GlobalResponseDto> updateUserPassword(@RequestBody UserUpdateReqDto userUpdateReqDto) {
        userService.updatePassword(userUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, "비밀번호 변경 완료"));
    }

    @GetMapping("/api/v1/users/check-email/{email}")
    public ResponseEntity<GlobalResponseDto> checkEmailDuplication(@PathVariable String email) {
        userService.checkEmailDuplication(email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, true));
    }


}