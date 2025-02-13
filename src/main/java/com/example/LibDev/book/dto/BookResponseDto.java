package com.example.LibDev.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {
    private String title;        // 도서 제목
    private String author;       // 저자
    private String publisher;    // 출판사
    private String thumbnail;    // 썸네일 이미지 URL
    private String publishedDate; // 발행일
    private String isbn;         // ISBN
    private String contents;     // 도서 소개


}
