package com.example.LibDev.borrow.service;

import com.example.LibDev.book.repository.BookRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    private static final int MAX_BORROW_LIMIT = 5; // 최대 대출 가능 권 수

    /* 대출 생성 */
    @Transactional
    public void borrow(Long bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("해당 책이 존재하지 않습니다."));

        checkMemberBorrowingStatus(user);

        Borrow borrow = Borrow.builder()
                .dueDate(LocalDateTime.now().plusDays(14))
                .returnDate(null)
                .extended(false)
                .overdue(false)
                .overdueDays(0)
                .status(Status.BORROWED)
                .bookId(bookId)
                .user(user)
                .build();

        borrowRepository.save(borrow);

        book.updateIsAvailable(false);

        updateBorrowAvailability(user);
    }

    /* 대출 중인 도서 권 수 기준 회원 대출 가능 여부 업데이트 */
    public void updateBorrowAvailability(User user) {
        int borrowedCount = borrowRepository.countByUserIdAndStatus(user.getId(), Status.BORROWED);

        if (borrowedCount >= MAX_BORROW_LIMIT) {
            user.updateBorrowAvailable(false);
            userRepository.save(user);
        }
    }

    /* 대출 기간 연장 */
    @Transactional
    public void extendReturnDate(Long borrowId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new CustomException(CustomErrorCode.BORROW_NOT_FOUND));
        Book book = bookRepository.findById(borrow.getBookId()).orElseThrow(() -> new RuntimeException("해당 책이 존재하지 않습니다."));

        // 예약자 존재 여부 확인
        if (reservationRepository.existsByBookAndStatus(book, ReservationStatus.WAITING)) {
            throw new CustomException(CustomErrorCode.EXTEND_FORBIDDEN);
        }

        checkMemberBorrowingStatus(user); // 회원 대출 가능 여부 확인

        borrow.extendDuedate(borrow.getDueDate().plusDays(7));
    }

    /* 회원 대출 가능 여부 검사 */
    public void checkMemberBorrowingStatus(User user) {
        if(!user.isBorrow_available()) {
            throw new CustomException(CustomErrorCode.BORROW_FORBIDDEN);
        }
    }
}
