package com.example.LibDev.auth.controller;

import com.example.LibDev.auth.dto.LoginReqDto;
import com.example.LibDev.auth.dto.ResetPasswordDto;
import com.example.LibDev.auth.dto.VerificationCodeDto;
import com.example.LibDev.auth.dto.VerificationEmailDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.auth.service.AuthService;
import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.global.util.CookieUtil;
import com.example.LibDev.user.repository.UserRepository;
import com.example.LibDev.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Collections;

@Slf4j
@RestController
public class AuthApiController {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthApiController(JwtProvider jwtProvider, UserRepository userRepository, AuthService authService) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/api/v3/auths/login")
    public ResponseEntity<Void> login(@RequestBody LoginReqDto loginReqDto){ return null; }

    @PostMapping("/api/v1/auths/logout")
    public ResponseEntity<Void> logout(){ return null; }

    @PostMapping("/api/v1/auths/reissue")
    public ResponseEntity<GlobalResponseDto> reissue(@CookieValue("refresh-token") String refreshToken){
        log.info("refresh token: {}", refreshToken);
        if(refreshToken == null || !jwtProvider.isValidToken(refreshToken)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponseDto.fail(
                            HttpStatus.UNAUTHORIZED,
                            "잘못된 접근입니다."
                    ));
        }

        String email = jwtProvider.getClaimsFromToken(refreshToken).getSubject();
        log.info(email);
        String role = jwtProvider.getClaimsFromToken(refreshToken).get("role").toString();
        log.info(role);
        long accessTokenValidTime = jwtProvider.getAccessTokenValidTime();

        if(!jwtProvider.isValidRefreshTokenInRedis(email, refreshToken)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponseDto.fail(
                            HttpStatus.UNAUTHORIZED,
                            "잘못된 접근입니다."
                    ));
        }

        String newAccessToken = jwtProvider.generateToken(email, role, "access-token", accessTokenValidTime);

        return ResponseEntity.status(HttpStatus.OK).header("Set-Cookie",
                        CookieUtil.createCookie("access-token",newAccessToken,accessTokenValidTime).toString())
                .body(GlobalResponseDto.success(HttpStatus.OK, "재발급 성공"));
    }

    // 현재 로그인된 유저의 userId 반환
    @GetMapping("/api/v1/auths/me")
    public ResponseEntity<Map<String, Long>> getCurrentUserId() {
        Authentication authentication = jwtProvider.getAuthenticationFromSecurityContext();
        String email = authentication.getName();

        if ("anonymousUser".equals(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("userId", null));
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("userId", null));
        }

        return ResponseEntity.ok(Collections.singletonMap("userId", user.getId()));
    }

    //인증번호 전송 요청
    @PostMapping("/api/v1/auth/password-find/verify")
    public ResponseEntity<GlobalResponseDto> verificationCodeTOEmail(@RequestBody VerificationEmailDto verificationEmailDto){
        String email = verificationEmailDto.getEmail();
        authService.sendVerificationCodeToEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(HttpStatus.CREATED, "메일 전송 완료"));
    }

    //인증번호 확인 후 임시 토큰 발급
    @PostMapping("/api/v1/auth/password-find/code")
    public ResponseEntity<GlobalResponseDto> verifyCode(@RequestBody VerificationCodeDto verificationCodeDto){
        String tempToken = authService.verifyCode(verificationCodeDto);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,
                        CookieUtil.createCookie("temp-token", tempToken, 600).toString())
                .body(GlobalResponseDto.success(HttpStatus.OK, "임시 토큰 발급"));
    }

    //임시토큰으로 비밀번호 변경
    @PatchMapping("/api/v1/auth/password-find/reset")
    public ResponseEntity<GlobalResponseDto> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                                           HttpServletRequest request){
        String tempToken = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("temp-token")) {
                tempToken = cookie.getValue();
            }
        }

        String email = jwtProvider.getClaimsFromToken(tempToken).getSubject();
        String password = resetPasswordDto.getPassword();

        authService.resetPassword(email, password);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,
                        CookieUtil.createCookie("temp-token", null, 0).toString())
                .body(GlobalResponseDto.success(HttpStatus.OK, "비밀번호 변경 완료"));
    }

}
