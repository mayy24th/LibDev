package com.example.LibDev.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    //global 에러
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "데이터 무결성 오류가 발생했습니다."),

    //user 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT,"이미 사용 중인 이메일입니다"),

    // borrow 관련 에러
    BORROW_NOT_FOUND(HttpStatus.NOT_FOUND, "대출 내역을 찾을 수 없습니다"),
    BORROW_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 회원은 대출이 불가합니다."),
    EXTEND_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 도서는 예약자가 있어 연장이 불가능합니다."),

    // reservation 관련 에러
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다"),
    BOOK_RESERVATION_FULL(HttpStatus.BAD_REQUEST, "해당 도서는 최대 예약 인원(5명)을 초과했습니다."),
    USER_RESERVATION_FULL(HttpStatus.BAD_REQUEST, "사용자는 최대 5권까지만 예약할 수 있습니다."),
    BOOK_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "이미 해당 책을 예약하셨습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),
    RESERVATION_CANCELLATION_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 예약만 취소할 수 있습니다."),
    USER_PENALIZED(HttpStatus.FORBIDDEN, "패널티가 적용된 사용자입니다. 예약이 불가능합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
