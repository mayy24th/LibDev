package com.example.LibDev.global.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CustomErrorResponseDto> handleCustomException(CustomException e) {
        return CustomErrorResponseDto.toResponseEntity(e.getCustomErrorCode());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<CustomErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();

        // UNIQUE 제약 조건 위반인지 확인
        if (message.contains("unique_user_book_reservation")) {
            return CustomErrorResponseDto.toResponseEntity(CustomErrorCode.BOOK_ALREADY_RESERVED);
        } else {
            return CustomErrorResponseDto.toResponseEntity(CustomErrorCode.DATA_INTEGRITY_VIOLATION);
        }
    }
}
