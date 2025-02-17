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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
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

        // 사용자의 penalty_expiration 체크
        if (user.getPenaltyExpiration() != null) {
            log.info("사용자 '{}'의 패널티가 적용되어 있어 모든 예약을 삭제합니다.", user.getEmail());
            deleteAllReservationsForUser(user);

            throw new CustomException(CustomErrorCode.USER_PENALIZED);
        }

        // 동일한 책을 중복 예약 방지
        if (reservationRepository.existsByUserAndBook(user, book)) {
            throw new CustomException(CustomErrorCode.BOOK_ALREADY_RESERVED);
        }

        // 예약 제한 체크
        checkUserReservationLimit(user);
        checkBookReservationLimit(book);

        int queueOrder = getNextQueueOrder(book);

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .status(ReservationStatus.WAITING)
                .reservedDate(LocalDateTime.now())
                .expirationDate(null)
                .queueOrder(queueOrder)
                .build();

        reservationRepository.save(reservation);

        updateFirstReservationExpiration(book);

        return reservation;
    }

    //다음예약자 만료일 업데이트
    @Transactional
    public void updateFirstReservationExpiration(Book book) {
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);

        if (!reservations.isEmpty()) {
            Reservation firstReservation = reservations.get(0);
            if (firstReservation.getExpirationDate() == null) {
                firstReservation.updateExpirationDate(LocalDateTime.now().plusDays(3));
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

    // 유저 별 예약 가능 여부 확인
    private void checkUserReservationLimit(User user) {
        int reservationCount = reservationRepository.countByUserAndStatus(user, ReservationStatus.WAITING);
        if (reservationCount >= MAX_USER_RESERVATION_LIMIT) {
            throw new CustomException(CustomErrorCode.USER_RESERVATION_FULL);
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

            // 예약 취소 버튼 API URL 설정
            String cancelUrl = "http://localhost:8080/api/v1/reservations/" + book.getBookId();

            content = "<h3>안녕하세요, " + user.getName() + "님!</h3>"
                    + "<p>회원님이 예약한 도서 '<b>" + book.getTitle() + "</b>'이(가) 대출 가능합니다.</p>"
                    + "<p>📅 예약 기한: <b>" + LocalDate.now().plusDays(3) + "</b></p>"
                    + "<p>3일 이내로 대출을 완료해주세요.</p>"
                    + "<br/><a href='" + cancelUrl + "' style='display:inline-block;padding:10px 20px;margin:10px;color:white;background-color:#f44336;text-decoration:none;border-radius:5px;'>예약 취소</a>";
//                    + "<br/><a href='http://localhost:8080/book/" + book.getBookId() + "' style='color:blue;'>도서 상세 정보 보기</a>";

            try {
                mailService.sendMail(to, subject, content);
            } catch (MessagingException e) {
                log.error("이메일 전송 실패: 수신자={}, 제목={}, 오류={}", to, subject, e.getMessage(), e);
            }
        }
    }

    // 패널티 체크
    @Transactional
    public void deleteAllReservationsForUser(User user) {
        List<Reservation> reservations = reservationRepository.findByUser(user);

        if (reservations.isEmpty()) {
            return;
        }

        log.info("사용자 '{}'의 모든 예약 {}건 삭제 진행", user.getEmail(), reservations.size());

        // 영향을 받은 book 목록을 저장하기 위한 Map (Book -> 첫 번째 예약 여부)
        Map<Book, Boolean> firstReservationMap = new HashMap<>();

        for (Reservation reservation : reservations) {
            Book book = reservation.getBook();

            // 현재 예약이 해당 도서의 첫 번째 예약자인지 확인
            List<Reservation> bookReservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
            boolean isFirstReservation = !bookReservations.isEmpty() && bookReservations.get(0).equals(reservation);

            // 첫 번째 예약자인 경우에만 Map에 저장 (나중에 한 번만 update 호출)
            if (isFirstReservation) {
                firstReservationMap.put(book, true);
            }

            // 예약 삭제
            reservationRepository.delete(reservation);
        }

        // 첫 번째 예약자가 삭제된 경우에만 만료일 업데이트 실행
        for (Map.Entry<Book, Boolean> entry : firstReservationMap.entrySet()) {
            updateFirstReservationExpiration(entry.getKey());
        }
        log.info("사용자 '{}'의 예약 삭제 완료 및 다음 예약자 만료일 업데이트 완료", user.getEmail());
    }



    // 예약 내역 조회
    public List<ReservationResponseDto> getUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        List<Reservation> reservations = reservationRepository.findByUser(user);

        return reservations.stream().map(reservation -> {
            int totalQueueSize = reservationRepository.countByBookAndStatus(reservation.getBook(), ReservationStatus.WAITING); // ✅ 해당 책의 예약 총 인원
            return ReservationResponseDto.fromEntity(reservation, totalQueueSize);
        }).collect(Collectors.toList());
    }


    // 예약 취소
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(CustomErrorCode.RESERVATION_CANCELLATION_FORBIDDEN);
        }

        Book book = reservation.getBook();

        // 현재 취소하는 예약자가 첫 번째 예약자인지 확인
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
        boolean isFirstReservation = !reservations.isEmpty() && reservations.get(0).equals(reservation);

        // 예약 삭제
        reservationRepository.delete(reservation);

        // 현재 예약이 첫 번째 예약자였던 경우에만 다음 예약자에게 만료일 설정 및 이메일 발송
        if (isFirstReservation) {
            updateFirstReservationExpiration(book);
        }
    }
}