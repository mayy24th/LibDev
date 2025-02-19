package com.example.LibDev.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReturnResDto {
    private final Long id;
    private final String status;
}
