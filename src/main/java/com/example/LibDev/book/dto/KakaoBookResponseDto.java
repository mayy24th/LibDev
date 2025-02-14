package com.example.LibDev.book.dto;

import com.example.LibDev.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoBookResponseDto {
    private String title;        // 도서 제목
    private String author;       // 저자
    private String publisher;    // 출판사
    private String thumbnail;    // 썸네일 이미지 URL
    private String publishedDate; // 발행일
    private String isbn;         // ISBN
    private String contents;     // 도서 소개

    // 엔티티 → DTO 변환 메서드
    public static KakaoBookResponseDto fromEntity(Book book) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return new KakaoBookResponseDto(
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedDate() != null ? book.getPublishedDate().format(formatter) : null, // 변환
                book.getIsbn(),
                book.getContents(),
                book.getThumbnail()
        );
    }
}
