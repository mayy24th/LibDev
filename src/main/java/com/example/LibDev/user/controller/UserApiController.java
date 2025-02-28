package com.example.LibDev.user.controller;

import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.global.util.BindingValidError;
import com.example.LibDev.global.util.CookieUtil;
import com.example.LibDev.user.dto.JoinReqDto;
import com.example.LibDev.user.dto.UserUpdateReqDto;
import com.example.LibDev.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;

    private static final String ACCESS_COOKIE_HEADER = "access-token";
    private static final String REFRESH_COOKIE_HEADER = "refresh-token";
    private static final long TOKEN_DEL = 0;

    @PostMapping("/api/v1/users")
    public ResponseEntity<GlobalResponseDto> join(@Valid @RequestBody JoinReqDto reqDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            HashMap<String, String> errors = BindingValidError.bindingValidError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GlobalResponseDto.fail(HttpStatus.BAD_REQUEST, errors));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(HttpStatus.CREATED,userService.join(reqDto)));
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<GlobalResponseDto> getUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, userService.info()));
    }

    @PatchMapping("/api/v1/users")
    public ResponseEntity<GlobalResponseDto> updateUser(@RequestBody UserUpdateReqDto userUpdateReqDto) {
        userService.update(userUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,"회원 정보 변경 완료되었습니다."));
    }
    //TODO: 하드 코딩으로 넣어진 문구는 message.properties 적용할 것

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
                .body(GlobalResponseDto.success(HttpStatus.OK, "사용 가능한 이메일입니다."));
    }

    @DeleteMapping("/api/v1/users")
    public ResponseEntity<GlobalResponseDto> deleteUsers(HttpServletRequest request) {
        userService.deleteUser(request);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(ACCESS_COOKIE_HEADER,null,TOKEN_DEL).toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(REFRESH_COOKIE_HEADER,null,TOKEN_DEL).toString())
                .body(GlobalResponseDto.success(HttpStatus.OK,"회원탈퇴 완료"));
    }

    @GetMapping("/api/v1/users/count")
    public ResponseEntity<GlobalResponseDto> countService() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, userService.getUserServiceCount()));
    }


}