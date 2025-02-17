package com.example.LibDev.review.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class ReviewDto {

    /** 한줄평 저장 요청 DTO **/
    // TODO : content 사이즈
    @Getter
    public static class SaveRequest {
        private Long bookId;
        @NotBlank
        private String content;
    }

    /** 한줄평 수정 요청 DTO **/
    @Getter
    public static class UpdateRequest {
        @NotBlank
        private String content;
    }

    /** 한줄평 응답 DTO **/
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String email;
        private String userName;
        private String bookName;
        private String content;
        private LocalDateTime createdAt; // 생성 시간
        private LocalDateTime updatedAt; // 수정 시간
    }
}