package com.example.LibDev.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BorrowDueDateResDto {
    private final Long bookId;
    private final LocalDateTime dueDate; // 반납 예정일
}
