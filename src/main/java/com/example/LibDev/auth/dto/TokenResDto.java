package com.example.LibDev.auth.dto;

import lombok.Getter;

@Getter
public class TokenResDto {
    private final String accessToken;
    private final String refreshToken;

    public TokenResDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
