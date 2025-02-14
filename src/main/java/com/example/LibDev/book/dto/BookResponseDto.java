package com.example.LibDev.book.dto;

import com.example.LibDev.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class BookResponseDto {
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String isbn;
    private String contents;
    private String callNumber;
    private String thumbnail;
    private Integer topicId;

    public static BookResponseDto fromEntity(Book book) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return new BookResponseDto(
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedDate() != null ? book.getPublishedDate().format(formatter) : null, // 발행일 변환
                book.getIsbn(),
                book.getContents(),
                book.getCallNumber(),
                book.getThumbnail(),
                book.getTopicId()
        );
    }
}
