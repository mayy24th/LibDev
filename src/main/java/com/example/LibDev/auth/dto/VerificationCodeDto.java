package com.example.LibDev.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerificationCodeDto {

    private String email;

    private String verificationCode;
}
