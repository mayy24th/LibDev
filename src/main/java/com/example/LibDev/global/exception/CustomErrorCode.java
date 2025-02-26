package com.example.LibDev.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    //global 에러
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "데이터 무결성 오류가 발생했습니다."),
    CODE_VERIFY_FAIL(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버에 오류가 발생했습니다."),

    //user 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT,"이미 사용 중인 이메일입니다"),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    USER_HAS_ACTIVE_BORROWS(HttpStatus.BAD_REQUEST,"대출 중인 도서가 있어 회원 탈퇴가 불가능합니다."),

    // borrow 관련 에러
    BORROW_NOT_FOUND(HttpStatus.NOT_FOUND, "대출 내역을 찾을 수 없습니다"),
    BOOK_BORROW_FORBIDDEN(HttpStatus.FORBIDDEN, "이 책은 현재 대출 중입니다. 페이지를 새로 고침 후 예약을 진행해주세요."),
    BORROW_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 회원은 대출이 불가합니다."),
    BORROW_OVERDUE(HttpStatus.FORBIDDEN, "연체로 인해 대출이 제한되었습니다."),
    BORROW_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "최대 대출 가능 권수를 초과했습니다."),
    EXTEND_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 도서는 예약자가 있어 연장이 불가능합니다."),
    ALREADY_BORROWED_BOOK(HttpStatus.FORBIDDEN,"현재 대출중이신 도서는 예약할 수 없습니다."),

    // reservation 관련 에러
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다"),
    BOOK_RESERVATION_FULL(HttpStatus.BAD_REQUEST, "해당 도서는 최대 예약 인원(7명)을 초과했습니다."),
    USER_RESERVATION_FULL(HttpStatus.BAD_REQUEST, "사용자는 최대 7권까지만 예약할 수 있습니다."),
    BOOK_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "이미 해당 책을 예약하셨습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),
    USER_PENALIZED(HttpStatus.FORBIDDEN, "패널티가 적용된 사용자입니다. 예약이 불가능합니다."),

    RESERVATION_CANCELLATION_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 예약만 취소할 수 있습니다."),
    BOOK_IS_AVAILABLE(HttpStatus.BAD_REQUEST, "대출 가능한 도서는 예약이 불가합니다."),

    // review 관련 에러
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "한줄평 정보를 찾을 수 없습니다."),
    REVIEW_DELETE_FORBIDDEN(HttpStatus.BAD_REQUEST, "본인이 작성한 한줄평만 삭제할 수 있습니다."),
    PROFANITY_DETECTED(HttpStatus.BAD_REQUEST, "비속어가 포함된 내용은 작성할 수 없습니다."),
    PROFANITY_FILTER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "비속어 필터 API 요청에 실패했습니다."),
    REVIEW_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "리뷰는 최대 100자까지 작성할 수 있습니다."),

    // Redis cache 관련 에러
    CACHE_CONVERSION_FAILED(HttpStatus.NOT_FOUND, "캐시 데이터 변환 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
