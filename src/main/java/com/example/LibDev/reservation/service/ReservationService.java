package com.example.LibDev.reservation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.reservation.dto.ReservationRequestDto;
import com.example.LibDev.reservation.dto.ReservationResponseDto;
import com.example.LibDev.reservation.service.MailService;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final MailService mailService;

    private static final int MAX_BOOK_RESERVATION_LIMIT = 5; // 특정 책 최대 예약 가능 인원
    private static final int MAX_USER_RESERVATION_LIMIT = 5; // 사용자 최대 예약 가능 권수

    // 예약 생성
    @Transactional
    public Reservation createReservation(ReservationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        // 동일한 책을 중복 예약 방지
        if (reservationRepository.existsByUserAndBook(user, book)) {
            throw new CustomException(CustomErrorCode.BOOK_ALREADY_RESERVED);
        }

        int queueOrder = getNextQueueOrder(book);
//        LocalDate expirationDate = LocalDate.now().plusDays(3);

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .status(ReservationStatus.WAITING)
                .reservedDate(LocalDate.now())
                .expirationDate(null)
                .queueOrder(queueOrder)
                .build();

        reservationRepository.save(reservation);


        updateFirstReservationExpiration(book);

        // 예약 이메일 발송
        sendReservationMail(user, book, queueOrder);

        return reservation;
    }

    private void updateFirstReservationExpiration(Book book) {
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);

        if (!reservations.isEmpty()) {
            Reservation firstReservation = reservations.get(0);
            if (firstReservation.getExpirationDate() == null) {
                firstReservation.updateExpirationDate(LocalDate.now().plusDays(3)); // ✅ 새 메서드 사용
                reservationRepository.save(firstReservation);

                // 첫 번째 예약자에게 이메일 알림 발송
                sendReservationMail(firstReservation.getUser(), book, firstReservation.getQueueOrder());
            }
        }
    }


    // 책 별 예약 가능 여부 확인
    private void checkBookReservationLimit(Book book) {
        int reservationCount = reservationRepository.countByBookAndStatus(book, ReservationStatus.WAITING);
        if (reservationCount >= MAX_BOOK_RESERVATION_LIMIT) {
            throw new CustomException(CustomErrorCode.BOOK_RESERVATION_FULL);
        }
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
            //문구 관리자페이지에서 관리할 수 있는 기능 추가
            subject = "📖 [도서 예약 안내] '" + book.getTitle() + "' 대출 가능합니다!";
            content = "<h3>안녕하세요, " + user.getName() + "님!</h3>"
                    + "<p>회원님이 예약한 도서 '<b>" + book.getTitle() + "</b>'이(가) 대출 가능합니다.</p>"
                    + "<p>📅 예약 기한: <b>" + LocalDate.now().plusDays(3) + "</b></p>"
                    + "<p>3일 이내로 대출을 완료해주세요.</p>";
//                    + "<br/><a href='http://localhost:8080/book/" + book.getBookId() + "' style='color:blue;'>도서 상세 정보 보기</a>";

            try {
                mailService.sendMail(to, subject, content);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }



    // 예약 내역 조회
    public List<ReservationResponseDto> getUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return reservationRepository.findByUser(user).stream()
                .map(ReservationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 예약 취소
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(CustomErrorCode.RESERVATION_CANCELLATION_FORBIDDEN);
        }

        reservationRepository.delete(reservation);

        // 예약 취소시, 다음 예약자의 expiration_date 설정
        updateFirstReservationExpiration(reservation.getBook());
    }
}