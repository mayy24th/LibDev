package com.example.LibDev.borrow.service;

import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.book.entity.Book;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;

    private static final int MAX_BORROW_LIMIT = 5; // 최대 대출 가능 권 수

    // 대출 생성
    @Transactional
    public void borrow(Long bookId/*, Long userId*/) {
        //User user = userRepository.findById(userId).get();

        // TODO : if(user.getBorrowAvailable() == false) // user의 대출 가능 여부 확인

        Borrow borrow = Borrow.builder()
                .dueDate(LocalDateTime.now().plusDays(14))
                .returnDate(null)
                .extended(false)
                .overdue(false)
                .bookId(bookId)
                //.user(user)
                .build();

        borrowRepository.save(borrow);

        // TODO : 책 대출 가능 여부 변경

        // updateBorrowAvailability(userId);
    }

    // 대출 중인 도서 권 수 기준 회원 대출 가능 여부 업데이트 메서드
    public void updateBorrowAvailability(Long userId) {
        int borrowedCount = borrowRepository.countByUserIdAndStatus(userId, Status.BORROWED);

        if (borrowedCount >= MAX_BORROW_LIMIT) {
            // TODO : borrow_available를 false로 업데이트
        }
    }
}
