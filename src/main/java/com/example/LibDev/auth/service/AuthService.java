package com.example.LibDev.auth.service;

import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    /*토큰 생성*/
    public TokenResDto generateToken(Authentication authentication) {
        TokenResDto tokenResDto = jwtProvider.generateToken(authentication);
        return tokenResDto;
    }
}
