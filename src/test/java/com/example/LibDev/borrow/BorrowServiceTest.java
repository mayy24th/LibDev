package com.example.LibDev.borrow;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.borrow.service.BorrowService;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import com.example.LibDev.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {
    private MockMvc mockMvc;

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @InjectMocks
    private BorrowService borrowService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("test1234")
                .name("testUser")
                .phone("123456789")
                .borrow_available(true)
                .penalty_expiration(null)
                .withdraw(false)
                .role(Role.USER)
                .build();

        book = Book.builder()
                .bookId(1L)
                .title("testTitle")
                .author("testAuthor")
                .publisher("testPublisher")
                .publishedDate(LocalDate.of(2025, 1, 1))
                .isbn("12345")
                .contents("testContent")
                .isAvailable(true)
                .callNumber("12345")
                .build();

        // SecurityContextHolder 설정
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }

    @Test
    void borrowBook() throws Exception {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // when
        borrowService.borrow(1L);

        // then
        Mockito.verify(borrowRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1)).save(Mockito.any());
        assertThat(book.getIsAvailable()).isFalse();
        assertThat(user.isBorrow_available()).isTrue();
    }
}
