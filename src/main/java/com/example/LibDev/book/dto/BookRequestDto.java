package com.example.LibDev.book.dto;

import com.example.LibDev.book.entity.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BookRequestDto {
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishedDate;
    private String isbn;
    private String contents;
    private Boolean isAvailable;
    private String callNumber;

    public Book toEntity() {
        return Book.builder()
                .title(this.title)
                .author(this.author)
                .publisher(this.publisher)
                .publishedDate(this.publishedDate)
                .isbn(this.isbn)
                .contents(this.contents)
                .isAvailable(this.isAvailable)
                .callNumber(this.callNumber)
                .build();
    }
}
