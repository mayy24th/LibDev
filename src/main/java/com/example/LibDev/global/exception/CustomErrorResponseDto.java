package com.example.LibDev.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class CustomErrorResponseDto {
    private final int statusCode;
    private final String code;
    private final String message;

    @Builder
    public CustomErrorResponseDto(int statusCode, String code, String message) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    //에러 응답
    public static ResponseEntity<CustomErrorResponseDto> toResponseEntity(CustomErrorCode customErrorCode) {
        return ResponseEntity
                .status(customErrorCode.getHttpStatus())
                .body(CustomErrorResponseDto.builder()
                        .statusCode(customErrorCode.getHttpStatus().value())
                        .code(customErrorCode.name())
                        .message(customErrorCode.getMessage())
                        .build()
                );
    }
}
