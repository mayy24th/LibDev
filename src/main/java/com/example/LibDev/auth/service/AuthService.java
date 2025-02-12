package com.example.LibDev.auth.service;

import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.global.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    /*토큰 생성*/
    public TokenResDto generateToken(Authentication authentication) {
        String email = authentication.getName();
        if(redisTokenService.getRefreshToken(email) != null){
            redisTokenService.delRefreshToken(email);
        }

        TokenResDto tokenResDto = jwtProvider.generateToken(authentication);
        saveRefreshToken(email,tokenResDto.getRefreshToken());
        return tokenResDto;
    }

    public void saveRefreshToken(String email, String refreshToken) {
        redisTokenService.setRefreshToken(
                email,
                refreshToken,
                jwtProvider.getRefreshTokenValidTime()
        );
    }
}
