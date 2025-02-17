package com.example.LibDev.auth.service;

import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.global.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    /*토큰 생성*/
    public TokenResDto generateToken(Authentication authentication) {
        String email = authentication.getName();
        String role =  authentication.getAuthorities().stream().findFirst().get().getAuthority();

        long accessTokenValidTime = jwtProvider.getAccessTokenValidTime();
        long refreshTokenValidTime = jwtProvider.getRefreshTokenValidTime();

        if(redisTokenService.getRefreshToken(email) != null){
            redisTokenService.delRefreshToken(email);
        }

        TokenResDto tokenResDto = TokenResDto.builder()
                .accessToken(jwtProvider.generateToken(email,role,"access-token",accessTokenValidTime))
                .refreshToken(jwtProvider.generateToken(email,role,"refresh-token",refreshTokenValidTime))
                .build();
        saveRefreshToken(email,tokenResDto.getRefreshToken());
        return tokenResDto;
    }

    /*리프레쉬 토큰 레디스에 저장*/
    public void saveRefreshToken(String email, String refreshToken) {
        redisTokenService.setRefreshToken(
                email,
                refreshToken,
                jwtProvider.getRefreshTokenValidTime()
        );
    }

    /*토큰 무효화*/
    public void deleteToken(String accessToken) {
        if (accessToken == null) {
            return;
        }

        String principal = getPrincipal(accessToken);
        long expiration = jwtProvider.getTokenValidTime(accessToken);

        String refreshTokenInReds = redisTokenService.getRefreshToken(principal);

        if(refreshTokenInReds != null){

            redisTokenService.delRefreshToken(principal);

            redisTokenService.setBlackList(accessToken,"logout",expiration - new Date().getTime());
        }
    }

    /*Principal 추출*/
    public String getPrincipal(String requestAccessToken) {
        return jwtProvider.getAuthentication(requestAccessToken).getName();
    }

}
