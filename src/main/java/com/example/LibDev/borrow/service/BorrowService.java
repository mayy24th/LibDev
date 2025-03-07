package com.example.LibDev.borrow.service;

import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.borrow.dto.*;
import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.recommendation.service.RecommendationCacheService;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.reservation.service.ReservationService;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final RecommendationCacheService recommendationCacheService;

    private static final int MAX_BORROW_LIMIT = 7;

    /* 회원별 대출 현황 조회 */
    public List<BorrowResDto> getCurrentBorrowsByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findLoginUserByEmail(email);
        log.debug("대출 현황 조회 회원:{}", email);

        if (user == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        List<Borrow> borrowList = borrowRepository.findByUserAndStatusNotOrderByIdDesc(user, Status.RETURNED);

        return borrowList.stream()
                .map(this::toBorrowResDto)
                .collect(Collectors.toList());
    }

    /* 회원별 대출 이력 조회 */
    public Page<BorrowResDto> getBorrowsByUser(int page, String order) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findLoginUserByEmail(email);
        log.debug("대출 이력 조회 회원:{}", email);

        if (user == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        Sort sort = "asc".equals(order) ? Sort.by("id").ascending() : Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Borrow> borrowList = borrowRepository.findByUserAndStatus(user, Status.RETURNED, pageable);

        return borrowList
                .map(this::toBorrowResDto);
    }

    /* 전체 대출 내역 조회 */
    public Page<BorrowResDto> getAllBorrows(int page, String status) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").descending());

        if("ALL".equals(status)) {
            Page<Borrow> borrowList = borrowRepository.findAll(pageable);
            return borrowList.map(this::toBorrowResDto);
        } else {
            Page<Borrow> borrowList = borrowRepository.findByStatus(Status.valueOf(status), pageable);
            return borrowList.map(this::toBorrowResDto);
        }
    }

    /* 대출 생성 */
    @Transactional
    public BorrowDueDateResDto borrow(Long bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findLoginUserByEmail(email);
        if (user == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));
        log.debug("대출 신청 - User Name: {}, Book Title: {}", user.getName(), book.getTitle());

        if (!book.getIsAvailable()) { // 대출 불가 상태
            if(!isFirstPriorityReservation(user, book)) {
                log.debug("대출 불가 - 이미 대출 중인 도서");
                throw new CustomException(CustomErrorCode.BOOK_BORROW_FORBIDDEN);
            }
            log.debug("대출 가능 - 1순위 예약자");
        }

        checkMemberBorrowingStatus(user); // 회원 대출 가능 여부 확인
        checkMaxBorrowLimit(user); // 최대 대출 가능 권 수 확인

        Borrow borrow = Borrow.builder()
                .dueDate(LocalDateTime.now().plusDays(14))
                .returnDate(null)
                .extended(false)
                .overdue(false)
                .overdueDays(0)
                .status(Status.BORROWED)
                .book(book)
                .user(user)
                .build();

        borrowRepository.save(borrow);
        recommendationCacheService.clearCache("user_base_books:" + email);
        book.updateIsAvailable(false);

        return BorrowDueDateResDto.builder()
                .bookId(book.getBookId())
                .dueDate(borrow.getDueDate())
                .build();
    }

    /* 대출 기간 연장 */
    @Transactional
    public ExtendResDto extendReturnDate(Long borrowId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        log.debug("대출 연장 - 대출자:{}", email);

        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new CustomException(CustomErrorCode.BORROW_NOT_FOUND));
        log.debug("대출 연장 - BorrowId:{}", borrow.getId());

        // 예약자 존재 여부 확인
        if (reservationRepository.existsByBookAndStatus(borrow.getBook(), ReservationStatus.WAITING)) {
            throw new CustomException(CustomErrorCode.EXTEND_FORBIDDEN);
        }

        checkMemberBorrowingStatus(user); // 회원 대출 가능 여부 확인

        borrow.extendDuedate(borrow.getDueDate().plusDays(7));
        borrow.updateExtended(true);

        return ExtendResDto.builder()
                .id(borrow.getId())
                .dueDate(borrow.getDueDate())
                .extended(borrow.isExtended())
                .build();
    }

    /* 도서 반납 신청 */
    @Transactional
    public ReturnResDto requestReturn(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new CustomException(CustomErrorCode.BORROW_NOT_FOUND));
        borrow.updateStatus(Status.RETURN_REQUESTED);

        return ReturnResDto.builder()
                .id(borrow.getId())
                .status(borrow.getStatus().getDescription())
                .build();
    }

    /* 도서 반납 승인 */
    @Transactional
    public List<ReturnResDto> approveReturn(ReturnApproveReqDto returnApproveReqDto) {
        List<Borrow> borrowList = borrowRepository.findAllById(returnApproveReqDto.getBorrowIds());

        if (borrowList.isEmpty()) {
            throw new CustomException(CustomErrorCode.BORROW_NOT_FOUND);
        }

        return borrowList.stream()
                .peek(this::processApproveReturn)
                .map(this::toReturnResDto)
                .collect(Collectors.toList());
    }

    /* 반납 승인 처리 로직 */
    private void processApproveReturn(Borrow borrow) {
        borrow.updateReturnDate(LocalDateTime.now());

        if (borrow.isOverdue()) {
            updateUserPenaltyExpiration(borrow.getUser(), borrow.getOverdueDays(), borrow.getReturnDate());
        }

        borrow.updateStatus(Status.RETURNED);
        updateBookIsAvailable(borrow.getBook());
    }


    /* 회원 패널티 만료일 업데이트 */
    public void updateUserPenaltyExpiration(User user, long overdueDays,  LocalDateTime returnDate) {
        if(user.getPenaltyExpiration() != null) {
            user.updatePenaltyExpiration(user.getPenaltyExpiration().plusDays(overdueDays));
        } else {
          user.updatePenaltyExpiration(returnDate.plusDays(overdueDays));
        }
    }

    /* 도서 대출 가능 여부 업데이트 */
    public void updateBookIsAvailable(Book book) {
        if (!reservationRepository.existsByBook(book)) {
            book.updateIsAvailable(true);
        } else {
            reservationService.processBookReturn(book);
        }
    }

    /* 1순위 예약자인지 검사 */
    public boolean isFirstPriorityReservation(User user, Book book) {
        List<Reservation> reservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);
        if (!reservations.isEmpty()) {
            Reservation firstReservation = reservations.getFirst(); // 현재 1순위 예약
            User firstReservationUser = firstReservation.getUser(); // 현재 1순위 예약자

            // 대출하려는 회원이 1순위 예약자인 경우
            return user.getEmail().equals(firstReservationUser.getEmail());
        }
        return false;
    }

    /* 회원 대출 가능 여부 검사 */
    public void checkMemberBorrowingStatus(User user) {
        if(!user.isBorrowAvailable()) {
            log.debug("대출(연장) 불가 - 연체 중 / 연체 피널티 존재");
            throw new CustomException(CustomErrorCode.BORROW_OVERDUE);
        }
    }

    /* 최대 대출 가능 권 수 초과 검사 */
    public void checkMaxBorrowLimit(User user) {
        int borrowedCount = borrowRepository.countByUserAndStatusNot(user, Status.RETURNED);

        if (borrowedCount >= MAX_BORROW_LIMIT) {
            log.debug("대출 불가 - 대출 가능 권 수 초과");
            throw new CustomException(CustomErrorCode.BORROW_LIMIT_EXCEEDED);
        }
    }

    /* 도서 반납 예정일 조회 */
    public BorrowDueDateResDto getBorrowDueDateByBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        Optional<Borrow> borrowOptional = borrowRepository.findByBookAndStatusNot(book, Status.RETURNED);

        return BorrowDueDateResDto.builder()
                .bookId(book.getBookId())
                .dueDate(borrowOptional.map(Borrow::getDueDate).orElse(null))
                .build();
    }

    /* entity -> borrowResDto 변환 */
    private BorrowResDto toBorrowResDto(Borrow borrow) {
        return BorrowResDto.builder()
                .id(borrow.getId())
                .bookTitle(borrow.getBook().getTitle())
                .callNumber(borrow.getBook().getCallNumber())
                .userEmail(borrow.getUser().getEmail())
                .status(borrow.getStatus().getDescription())
                .borrowDate(borrow.getCreatedAt())
                .dueDate(borrow.getDueDate())
                .returnDate(borrow.getReturnDate())
                .extended(borrow.isExtended())
                .overdue(borrow.isOverdue())
                .overdueDays(borrow.getOverdueDays())
                .borrowAvailable(borrow.getUser().isBorrowAvailable())
                .build();
    }

    /* entity -> returnResDto 변환 */
    private ReturnResDto toReturnResDto(Borrow borrow) {
        return ReturnResDto.builder()
                .id(borrow.getId())
                .status(borrow.getStatus().getDescription())
                .returnDate(borrow.getReturnDate())
                .build();
    }
}
