package com.example.LibDev.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    //user 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),

    // borrow 관련 에러
    BORROW_NOT_FOUND(HttpStatus.NOT_FOUND, "대출 내역을 찾을 수 없습니다"),
    BORROW_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 회원은 대출이 불가합니다."),
    EXTEND_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 도서는 예약자가 있어 연장이 불가능합니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
