package com.example.LibDev.auth.controller;

import com.example.LibDev.auth.dto.LoginReqDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.global.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthApiController {

    private final JwtProvider jwtProvider;

    public AuthApiController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
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
}
