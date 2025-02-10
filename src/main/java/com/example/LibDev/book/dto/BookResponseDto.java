package com.example.LibDev.book.dto;

import com.example.LibDev.book.entity.Book;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class BookResponseDto {
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishedDate;
    private String isbn;
    private String contents;
    private String callNumber;
    private Boolean isAvailable;

    // Entity -> DTO 변환
    public BookResponseDto(Book book) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.publishedDate = book.getPublishedDate();
        this.isbn = book.getIsbn();
        this.contents = book.getContents();
        this.isAvailable = book.getIsAvailable();
        this.callNumber = book.getCallNumber();
    }
}
