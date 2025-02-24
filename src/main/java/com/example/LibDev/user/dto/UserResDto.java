package com.example.LibDev.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserResDto {

    private final String name;
    private final String email;
    private final String phone;
    private final String role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;


}
