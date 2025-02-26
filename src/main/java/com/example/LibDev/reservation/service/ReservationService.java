package com.example.LibDev.reservation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.borrow.service.BorrowService;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.notification.service.NotificationService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private final NotificationService notificationService;
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
            boolean isFirstReservation = !bookReservations.isEmpty() && bookReservations.getFirst().equals(reservation);

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

    //
    private void notifyReservationUser(Long userId, Reservation reservation, String message) {
        String finalMessage = message + " (도서 ID: " + reservation.getBook().getBookId() + ")";
        notificationService.sendReservationNotification(userId, finalMessage);
    }


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

    public String getBorrowingUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/reservations/list")
                .toUriString();
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
            String borrowingUrl = getBorrowingUrl();

            content = "<h3>안녕하세요, " + user.getName() + "님!</h3>"
                    + "<p>회원님이 예약한 도서 '<b>" + book.getTitle() + "</b>'이(가) 대출 가능합니다.</p>"
                    + "<p>📅 예약 기한: <b>" + LocalDate.now().plusDays(3) + "</b></p>"
                    + "<p>3일 이내로 대출을 완료해주세요.</p>"
                    + "<br/><a href='" + borrowingUrl + "' style='display:inline-block;padding:10px 20px;margin:10px;color:white;background-color:#f44336;text-decoration:none;border-radius:5px;'>대출 및 예약 취소</a>";
            try {
                mailService.sendMail(to, subject, content);
            } catch (MessagingException e) {
                log.error("이메일 전송 실패: 수신자={}, 제목={}, 오류={}", to, subject, e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateFirstReservationExpiration(Book book) {
        // 해당 책의 대출 상태 조회
        boolean hasActiveBorrow = borrowRepository.existsByBookAndStatusNot(book, Status.RETURNED);
        log.info("특정 도서의 hasActiveBorrow 값: {} (도서 ID: {})", hasActiveBorrow, book.getBookId());

        if (hasActiveBorrow) {
            log.info("현재 대출 중인 책. 예약자 업데이트 중단 (도서 ID: {})", book.getBookId());
            return;
        }


        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);

        if (reservations.isEmpty()) {
            log.info("예약이 없는 책입니다. 만료일 업데이트 및 이메일 전송 없음: {}", book.getTitle());
            return;
        }

        Reservation firstReservation = reservations.getFirst();
        log.info("새로운 1순위 예약자: {} (User ID: {}, Expiration: {})",
                firstReservation.getUser().getEmail(), firstReservation.getUser().getId(), firstReservation.getExpirationDate());

        boolean isNewFirstReservation = firstReservation.getExpirationDate() == null;

        // 만료일이 없는 경우 업데이트
        if (isNewFirstReservation) {
            firstReservation.updateExpirationDate(LocalDateTime.now().plusDays(3));
            reservationRepository.save(firstReservation);
            log.info("새로운 1순위 예약자 만료일 설정 완료: {}", firstReservation.getExpirationDate());
        }

        // todo : ready 인지 추가 체크 필요!!!!
        // 조건 체크 후 이메일 발송 + 알림 발송
        if (isNewFirstReservation || firstReservation.getQueueOrder() == 1) {
            log.info("이메일 발송 시작: {}", firstReservation.getUser().getEmail());

            // status Ready로 변경
            firstReservation.setStatus(ReservationStatus.READY);
            reservationRepository.save(firstReservation); // 변경 사항 저장
            log.info("예약 상태 변경: WAITING -> READY (User ID: {})", firstReservation.getUser().getId());

            // 이메일 발송
            sendReservationMail(firstReservation.getUser(), book, firstReservation.getQueueOrder());

            // 알림 발송
            notifyReservationUser(
                    firstReservation.getUser().getId(),
                    firstReservation,
                    "예약하신 도서 '" + book.getTitle() + "'이 대출 가능 상태가 되었습니다!"
            );
        } else {
            log.info("이메일 발송 조건 미충족: queueOrder={}, Expiration={}",
                    firstReservation.getQueueOrder(), firstReservation.getExpirationDate());
        }
    }

    // 예약 내역 조회
    public List<ReservationResponseDto> getUserReservationsWithCanBorrow(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Reservation> reservations = reservationRepository.findByUser(user);

        return reservations.stream().map(reservation -> {
            int totalQueueSize = reservationRepository.countByBook(reservation.getBook()); // 총 예약 수 계산

            // 현재 책이 'returned' 상태가 아닌 다른 상태가 있는지 체크
            boolean hasActiveBorrow = borrowRepository.existsByBookAndStatusNot(reservation.getBook(), Status.RETURNED);

            boolean isFirstQueue = reservation.getQueueOrder() == 1; // 예약 1순위 여부 확인
            boolean canBorrow = !hasActiveBorrow && isFirstQueue; // 대출 가능 여부 판단

            return ReservationResponseDto.fromEntity(reservation, totalQueueSize, canBorrow);
        }).collect(Collectors.toList());
    }


    // 전체 예약 내역 조회
    public Page<ReservationResponseDto> getAllReservations(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Reservation> reservations = reservationRepository.findAll(pageable);

        return reservations.map(reservation -> {
            int totalQueueSize = reservationRepository.countByBook(reservation.getBook()); // 총 예약자 수 계산

            boolean hasActiveBorrow = borrowRepository.existsByUserAndBookAndStatusNot(reservation.getUser(), reservation.getBook(), Status.RETURNED);

            boolean isFirstQueue = reservation.getQueueOrder() == 1; // 예약 1순위 확인
            boolean canBorrow = !hasActiveBorrow && isFirstQueue; // 대출 가능 여부 판단

            return ReservationResponseDto.fromEntity(reservation, totalQueueSize, canBorrow);
        });
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
        boolean isFirstReservation = !reservations.isEmpty() && reservations.getFirst().equals(reservation);

        // 예약 삭제
        reservationRepository.delete(reservation);

        // 예약 대기열 재정렬 (queueOrder 다시 1부터 부여)
        List<Reservation> updatedReservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
        for (int i = 0; i < updatedReservations.size(); i++) {
            updatedReservations.get(i).updateQueueOrder(i + 1); // 1부터 다시 부여
            reservationRepository.save(updatedReservations.get(i));
        }

        // todo : ready 체크!!
        // 현재 예약이 첫 번째 예약자였던 경우에만 다음 예약자에게 만료일 설정 및 이메일 발송 및 알림발송
        if (isFirstReservation) {
            updateFirstReservationExpiration(book);
        }
    }

    /* 도서 대출 가능 여부 업데이트 */
    public void updateBookIsAvailable(Book book) {
        if (!reservationRepository.existsByBook(book)) {
            book.updateIsAvailable(true);
        }
    }

    // 도서 반납시 예약 처리
    @Transactional
    public void processBookReturn(Book book) {
        log.info("도서 '{}' 반납 처리 시작", book.getTitle());

        // 첫 번째 예약자 확인
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
        if (!reservations.isEmpty()) {
            Reservation firstReservation = reservations.getFirst(); // 현재 1순위 예약자
            log.info("반납 후 첫 번째 예약자 확인: {} (User ID: {})", firstReservation.getUser().getEmail(), firstReservation.getUser().getId());

            // 첫 번째 예약자에 대한 만료일 업데이트 및 이메일 발송
            updateFirstReservationExpiration(book);
        } else {
            log.info("반납 후 예약자가 없음. 업데이트 생략.");
            updateBookIsAvailable(book);
        }
    }


    /*// READY 상태로 변경될 때 알림 전송 추가
    private void notifyNextUser(Book book) {
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
        if (!reservations.isEmpty()) {
            Reservation nextReservation = reservations.getFirst();
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