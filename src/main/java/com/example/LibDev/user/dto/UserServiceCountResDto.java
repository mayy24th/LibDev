package com.example.LibDev.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserServiceCountResDto {
    private int borrowCount;
    private int returnCount;
    private int reservationCount;
    private int reviewCount;
}
