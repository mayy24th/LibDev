package com.example.LibDev.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CustomErrorResponseDto> handleCustomException(CustomException e) {
        return CustomErrorResponseDto.toResponseEntity(e.getCustomErrorCode());
    }
}
