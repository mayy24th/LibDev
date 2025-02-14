package com.example.LibDev.book.dto;

import com.example.LibDev.book.entity.Book;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String isbn;
    private String contents;
    private String thumbnail;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;

    private String callNumber;
    private String topicId;

    public Book toEntity() {
        return Book.builder()
                .title(this.title)
                .author(this.author)
                .publisher(this.publisher)
                .isbn(this.isbn)
                .contents(this.contents)
                .publishedDate(this.publishedDate)
                .callNumber(this.callNumber)
                .topicId(Integer.valueOf(this.topicId))
                .thumbnail(this.thumbnail)
                .build();
    }
}