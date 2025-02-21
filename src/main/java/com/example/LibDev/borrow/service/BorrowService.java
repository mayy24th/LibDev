package com.example.LibDev.borrow.service;

import com.example.LibDev.book.repository.BookRepository;
import com.example.LibDev.borrow.dto.BorrowResDto;
import com.example.LibDev.borrow.dto.ExtendResDto;
import com.example.LibDev.borrow.dto.ReturnResDto;
import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    private static final int MAX_BORROW_LIMIT = 5; // 최대 대출 가능 권 수

    /* 회원별 대출 현황 조회 */
    public List<BorrowResDto> getCurrentBorrowsByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findLoginUserByEmail(email);

        if (user == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        List<Borrow> borrowList = borrowRepository.findByUserAndStatusNot(user, Status.RETURNED);

        return borrowList.stream()
                .map(this::toBorrowResDto)
                .collect(Collectors.toList());
    }

    /* 전체 대출 내역 조회 */
    public List<BorrowResDto> getAllBorrows() {
        List<Borrow> borrowList = borrowRepository.findAll();

        return borrowList.stream()
                .map(this::toBorrowResDto)
                .collect(Collectors.toList());
    }

    /* 대출 생성 */
    @Transactional
    public void borrow(Long bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("해당 책이 존재하지 않습니다."));
        log.debug("대출 신청 - User Name: {}, Book Title: {}", user.getName(), book.getTitle());

        checkMemberBorrowingStatus(user);
        checkMaxBorrowLimit(user);

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

        book.updateIsAvailable(false);
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
    public ReturnResDto approveReturn(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new CustomException(CustomErrorCode.BORROW_NOT_FOUND));

        borrow.updateReturnDate(LocalDateTime.now());

        if(borrow.getStatus() == Status.OVERDUE) {
            borrow.updateOverdueDays(ChronoUnit.DAYS.between(borrow.getDueDate(), borrow.getReturnDate()));
            updateUserPenaltyExpiration(borrow.getUser(), borrow.getOverdueDays(), borrow.getReturnDate());
        }

        borrow.updateStatus(Status.RETURNED);

        updateBookIsAvailable(borrow.getBook());

        return ReturnResDto.builder()
                .id(borrow.getId())
                .status(borrow.getStatus().getDescription())
                .returnDate(borrow.getReturnDate())
                .build();
    }

    /* 회원 패널티 만료일 업데이트 */
    public void updateUserPenaltyExpiration(User user, long overdueDays,  LocalDateTime returnDate) {
        if(user.getPenaltyExpiration() != null) {
            user.updatePenaltyExpiration(user.getPenaltyExpiration().plusDays(overdueDays));
        } else {
          user.setPenaltyExpiration(returnDate.plusDays(overdueDays));
        }
    }

    /* 도서 대출 가능 여부 업데이트 */
    public void updateBookIsAvailable(Book book) {
        if (!reservationRepository.existsByBook(book)) {
            book.updateIsAvailable(true);
        }
    }

    /* 회원 대출 가능 여부 검사 */
    public void checkMemberBorrowingStatus(User user) {
        if(!user.isBorrowAvailable()) {
            log.debug("대출(연장) 불가 - 연체 중 / 연체 피널티 존재");
            throw new CustomException(CustomErrorCode.BORROW_FORBIDDEN);
        }
    }

    /* 최대 대출 가능 권 수 초과 검사 */
    public void checkMaxBorrowLimit(User user) {
        int borrowedCount = borrowRepository.countByUserAndStatus(user, Status.BORROWED);

        if (borrowedCount >= MAX_BORROW_LIMIT) {
            log.debug("대출 불가 - 대출 가능 권 수 초과");
            throw new CustomException(CustomErrorCode.BORROW_FORBIDDEN);
        }
    }

    /* entity -> borrowResDto 변환 */
    public BorrowResDto toBorrowResDto(Borrow borrow) {
        return BorrowResDto.builder()
                .id(borrow.getId())
                .bookTitle(borrow.getBook().getTitle())
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
}
