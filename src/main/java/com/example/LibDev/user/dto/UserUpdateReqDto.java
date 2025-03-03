package com.example.LibDev.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserUpdateReqDto {
    private String password;
    private String name;
    private String phone;
}
