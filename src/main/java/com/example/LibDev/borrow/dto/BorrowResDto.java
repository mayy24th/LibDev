package com.example.LibDev.borrow.dto;

import com.example.LibDev.borrow.entity.type.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BorrowResDto {
    private final Long id;
    private final String bookTitle;
    private final Status status; // 대출 상태
    private final LocalDateTime borrowDate; // 대출일
    private final LocalDateTime dueDate; // 반납 예정일
    private final LocalDateTime returnDate; // 실제 반납일
    private final boolean extended; // 연장 여부
    private final boolean overdue; // 연체 여부
    private final int overdueDays; // 연체일 수
}
