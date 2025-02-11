package com.example.LibDev.review.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewDto {

    /** 한줄평 저장 요청 DTO **/
    // TODO : content 사이즈
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SaveRequest {
        private Long userId;
        private Long bookId;
        @NotBlank
        private String content;
    }

    /** 한줄평 삭제 요청 DTO **/
    @Getter
    @Builder
    @AllArgsConstructor
    public static class DeleteRequest {
        private Long id;
    }

    /** 한줄평 수정 요청 DTO **/
    @Getter
    @Builder
    public static class UpdateRequest {
        @NotBlank
        private String content;
    }

    /** 한줄평 응답 DTO **/
    @Getter
    @Builder
    public static class Response {
        private Long id;
        // TODO : 유저, 도서정보 id -> 유저, 도서 객체
        // private User user;
        // private Book book;
        private Long userId;
        private Long bookId;
        private String content;
        private LocalDateTime createdAt; // 생성 시간
        private LocalDateTime updatedAt; // 생성 시간
    }
}