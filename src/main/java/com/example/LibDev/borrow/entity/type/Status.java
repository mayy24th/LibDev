package com.example.LibDev.borrow.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    BORROWED("대출 중"),
    RETURNED("반납 완료"),
    OVERDUE("연체 중");

    private final String description;
}
