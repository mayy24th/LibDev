package com.example.LibDev.auth.controller;

import com.example.LibDev.auth.dto.LoginReqDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.global.util.CookieUtil;
import com.example.LibDev.user.repository.UserRepository;
import com.example.LibDev.user.entity.User;
import lombok.extern.slf4j.Slf4j;
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

    public AuthApiController(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
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
}
