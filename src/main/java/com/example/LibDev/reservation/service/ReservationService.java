package com.example.LibDev.reservation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.notification.service.MailService;
import com.example.LibDev.reservation.dto.ReservationRequestDto;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final MailService mailService; // 추가

    // 예약 생성
    public Reservation createReservation(ReservationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User 가 없습니다."));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book 이 없습니다."));

        int queueOrder = getNextQueueOrder(book);
        LocalDate expirationDate = LocalDate.now().plusDays(3);

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .status(ReservationStatus.WAITING)
                .reservedDate(LocalDate.now())
                .expirationDate(expirationDate)
                .queueOrder(queueOrder)
                .build();

        reservationRepository.save(reservation);

        // 예약 이메일 발송
        sendReservationMail(user, book, queueOrder);

        return reservation;
    }

    // 예약 대기 순번 계산
    private int getNextQueueOrder(Book book) {
        return reservationRepository.findByBookOrderByQueueOrderAsc(book).size() + 1;
    }

    // 예약 알림 이메일 발송
    private void sendReservationMail(User user, Book book, int queueOrder) {
        String to = user.getEmail();
        String subject;
        String content;

        if (queueOrder == 1) {
            subject = "📖 [도서 예약 안내] '" + book.getTitle() + "' 대출 가능합니다!";
            content = "<h3>안녕하세요, " + user.getName() + "님!</h3>"
                    + "<p>회원님이 예약한 도서 '<b>" + book.getTitle() + "</b>'이(가) 대출 가능합니다.</p>"
                    + "<p>📅 예약 기한: <b>" + LocalDate.now().plusDays(3) + "</b></p>"
                    + "<p>3일 이내로 대출을 완료해주세요.</p>"
                    + "<br/><a href='http://localhost:8080/book/" + book.getBookId() + "' style='color:blue;'>도서 상세 정보 보기</a>";

            try {
                mailService.sendMail(to, subject, content);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}