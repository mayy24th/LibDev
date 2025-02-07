package com.example.LibDev.book.service;

import com.example.LibDev.book.dto.BookRequestDto;
import com.example.LibDev.book.dto.BookResponseDto;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    /*
    // 도서 직접 등록
    @Transactional
    public Book saveBook(BookRequestDto bookRequestDto) {
        Book book = bookRequestDto.toEntity();
        return bookRepository.save(book);
    }
    */

    private final RestTemplate restTemplate = new RestTemplate(); // API 호출을 위한 RestTemplate

    // Kakao API에서 도서 정보를 가져와 DB에 저장
    public void saveBookFromKakao(String query) {
        String url = "https://dapi.kakao.com/v3/search/book?query=" + query;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK 77a416b84bd8bc56ccf085a8b028dce6"); // 실제 API 키로 변경

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 응답을 Map으로 받기
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        System.out.println("Kakao API Response: " + responseBody);

        if (responseBody != null) {
            Object documentsObj = responseBody.get("documents");
            System.out.println("Documents Object: " + documentsObj);

            if (documentsObj instanceof List) {
                List<Map<String, Object>> books = (List<Map<String, Object>>) documentsObj;

                // 도서 정보 타입 변환
                for (Map<String, Object> bookData : books) {
                    String title = String.valueOf(bookData.get("title"));
                    String author = String.valueOf(bookData.get("authors"));
                    String publisher = String.valueOf(bookData.get("publisher"));
                    String isbn = String.valueOf(bookData.get("isbn"));
                    String datetime = String.valueOf(bookData.get("datetime"));
                    String contents = String.valueOf(bookData.get("contents"));

                    // 날짜 형식 파싱: LocalDateTime으로 수정
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    LocalDateTime publishedDate = LocalDateTime.parse(datetime, formatter);

                    // 도서 정보 DB에 저장
                    Book book = Book.builder()
                            .title(title)
                            .author(author)
                            .publisher(publisher)
                            .isbn(isbn)
                            .publishedDate(publishedDate.toLocalDate())  //LocalDate로 변환
                            .contents(contents)
                            .isAvailable(true)
                            .callNumber("N/A")
                            .build();

                    bookRepository.save(book);
                    //break;  // 첫 번째 책만 저장
                }
            } else {
                throw new IllegalStateException("Invalid response format: documents is not a List");
            }
        }
    }
}
