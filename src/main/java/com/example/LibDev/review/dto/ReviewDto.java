package com.example.LibDev.review.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ReviewDto {

    /** 리뷰 저장 요청 DTO **/
    // TODO : content 사이즈
    @Getter
    @Builder
    public static class SaveRequest {
        private Long userId;
        private Long bookId;
        @NotBlank
        private String content;
    }

    /** 리뷰 삭제 요청 DTO **/
    @Getter
    @Builder
    @AllArgsConstructor
    public static class DeleteRequest {
        private Long id;
    }

    /** 리뷰 수정 요청 DTO **/
    @Getter
    @Builder
    public static class UpdateRequest {
        @NotBlank
        private String content;
    }

    /** 리뷰 응답 DTO **/
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long userId;
        private Long bookId;
        private String content;
        private LocalDateTime createdAt; // 생성 시간
        private LocalDateTime updatedAt; // 생성 시간
    }
}