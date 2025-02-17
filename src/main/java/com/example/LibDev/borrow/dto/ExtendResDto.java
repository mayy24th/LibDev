package com.example.LibDev.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ExtendResDto {
    private final Long id;
    private final LocalDateTime dueDate; // 반납 예정일
    private final boolean extended; // 연장 여부
}
