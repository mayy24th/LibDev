package com.example.LibDev.reservation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.reservation.dto.ReservationRequestDto;
import com.example.LibDev.reservation.dto.ReservationResponseDto;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final MailService mailService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int MAX_BOOK_RESERVATION_LIMIT = 7; // 특정 책 최대 예약 가능 인원
    private static final int MAX_USER_RESERVATION_LIMIT = 7; // 사용자 최대 예약 가능 권수

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

    // 책 별 예약 가능 여부 확인
    private void checkBookReservationLimit(Book book) {
        int reservationCount = reservationRepository.countByBookAndStatus(book, ReservationStatus.WAITING);
        if (reservationCount >= MAX_BOOK_RESERVATION_LIMIT) {
            throw new CustomException(CustomErrorCode.BOOK_RESERVATION_FULL);
        }
    }

    // 유저 별 예약 가능 여부 + 대출 중인 책인지 확인
    private void checkUserReservationLimit(User user, Book book) {
        int reservationCount = reservationRepository.countByUserAndStatus(user, ReservationStatus.WAITING);
        if (reservationCount >= MAX_USER_RESERVATION_LIMIT) {
            throw new CustomException(CustomErrorCode.USER_RESERVATION_FULL);
        }

        // 반납 완료(RETURNED)가 아닌 대출 내역이 있는지 확인
        boolean hasUnreturnedBorrow = borrowRepository.existsByUserAndBookAndStatusNot(user, book, Status.RETURNED);

        if (hasUnreturnedBorrow) {
            throw new CustomException(CustomErrorCode.ALREADY_BORROWED_BOOK);
        }
    }



    // 예약 대기 순번 계산
    private int getNextQueueOrder(Book book) {
        return reservationRepository.findByBookOrderByQueueOrderAsc(book).size() + 1;
    }

    // WebSocket을 통해 특정 유저에게 알림 전송
    private void sendReservationNotification(Long userId, Reservation reservation, String message) {
        String destination = "/topic/reservations/" + userId;
        String finalMessage = message + " (도서 ID: " + reservation.getBook().getBookId() + ")";
        messagingTemplate.convertAndSend(destination, new ReservationNotification(finalMessage));
        log.info("예약 알림 전송: {} -> {}", finalMessage, destination);
    }


    /* 예약 알림 DTO */
    public record ReservationNotification(String message) {}

    // 예약 생성
    @Transactional
    public Reservation createReservation(ReservationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        // 대출 가능 여부 체크 (대출 가능하면 예약 불가)
        if (book.getIsAvailable()) {
            throw new CustomException(CustomErrorCode.BOOK_IS_AVAILABLE);
        }

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
        checkUserReservationLimit(user, book);
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

        if (queueOrder == 1) {
            updateFirstReservationExpiration(book);
        }

        return reservation;
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

    @Transactional
    public void updateFirstReservationExpiration(Book book) {
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);

        if (reservations.isEmpty()) {
            log.info("예약이 없는 책입니다. 만료일 업데이트 및 이메일 전송 없음: {}", book.getTitle());
            return;
        }

        Reservation firstReservation = reservations.get(0);
        log.info("새로운 1순위 예약자: {} (User ID: {}, Expiration: {})",
                firstReservation.getUser().getEmail(), firstReservation.getUser().getId(), firstReservation.getExpirationDate());

        boolean isNewFirstReservation = firstReservation.getExpirationDate() == null;

        // 만료일이 없는 경우 업데이트
        if (isNewFirstReservation) {
            firstReservation.updateExpirationDate(LocalDateTime.now().plusDays(3));
            reservationRepository.save(firstReservation);
            log.info("새로운 1순위 예약자 만료일 설정 완료: {}", firstReservation.getExpirationDate());
        }

        // 이메일 발송 전 확인 (queueOrder가 1인지 체크)
        log.info("이메일 발송 체크: 예약자 Queue Order: {}", firstReservation.getQueueOrder());

        // 조건 체크 후 이메일 발송
        if (isNewFirstReservation || firstReservation.getQueueOrder() == 1) {
            log.info("이메일 발송 시작: {}", firstReservation.getUser().getEmail());
            sendReservationMail(firstReservation.getUser(), book, firstReservation.getQueueOrder());
        } else {
            log.info("이메일 발송 조건 미충족: queueOrder={}, Expiration={}",
                    firstReservation.getQueueOrder(), firstReservation.getExpirationDate());
        }
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

        // 예약 대기열 재정렬 (queueOrder 다시 1부터 부여)
        List<Reservation> updatedReservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
        for (int i = 0; i < updatedReservations.size(); i++) {
            updatedReservations.get(i).updateQueueOrder(i + 1); // 1부터 다시 부여
            reservationRepository.save(updatedReservations.get(i));
        }


        // 현재 예약이 첫 번째 예약자였던 경우에만 다음 예약자에게 만료일 설정 및 이메일 발송
        if (isFirstReservation) {
            updateFirstReservationExpiration(book);
        }
    }

    // 도서 반납시 예약 처리
    @Transactional
    public void updateBookAvailability(Book book, boolean isAvailable) {
        book.updateIsAvailable(isAvailable);
        bookRepository.save(book);

        // 도서가 반납(isAvailable = true)되었을 때, 첫 번째 예약자 확인 후 만료일 및 이메일 발송
        if (isAvailable) {
            log.info("도서 '{}'가 반납됨. 예약자 확인 후 이메일 발송", book.getTitle());

            List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
            if (!reservations.isEmpty()) {
                Reservation firstReservation = reservations.get(0); // 현재 1순위 예약자
                log.info("반납 후 첫 번째 예약자: {} (User ID: {})",
                        firstReservation.getUser().getEmail(), firstReservation.getUser().getId());

                updateFirstReservationExpiration(book); // 첫 번째 예약자에게 만료일 설정 및 이메일 발송
            } else {
                log.info("반납 후 예약자가 없음. 이메일 발송 생략.");
            }
        }
    }

    /*// READY 상태로 변경될 때 알림 전송 추가
    private void notifyNextUser(Book book) {
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
        if (!reservations.isEmpty()) {
            Reservation nextReservation = reservations.get(0);
            nextReservation.setStatus(ReservationStatus.READY);
            reservationRepository.save(nextReservation);

            // 웹소켓을 통해 사용자에게 알림 전송
            notificationService.sendReservationNotification(
                    nextReservation.getUser().getId(),
                    "예약하신 도서 '" + book.getTitle() + "'이 대출 가능 상태가 되었습니다!"
            );
        }
    }*/

    // 특정 도서의 예약자 수(WAITING 상태) 조회 메서드 추가
    @Transactional(readOnly = true)
    public int getReservationCountByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));
        return reservationRepository.countByBookAndStatus(book, ReservationStatus.WAITING);
    }

}