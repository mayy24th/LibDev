package com.example.LibDev.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class TokenResDto {
    private final String accessToken;
    private final String refreshToken;

}
