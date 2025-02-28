package com.example.LibDev.borrow.service;

import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OverdueService {
    private final BorrowRepository borrowRepository;

    /* 연체 처리 */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void overdue() {
        List<Borrow> borrows = borrowRepository.findByStatusAndDueDateBefore(Status.BORROWED, LocalDateTime.now());

        borrows.forEach(borrow -> {
            User user = borrow.getUser();
            if (user.isBorrowAvailable()) {
                user.updateBorrowAvailable(false);
            }

            borrow.updateStatus(Status.OVERDUE);
            borrow.updateOverdue(true);
        });

        log.info("연체된 대출 {}건 업데이트", borrows.size());

        increaseOverdueDays();
    }

    @Transactional
    public void increaseOverdueDays() {
        List<Borrow> overdueBorrows = borrowRepository.findByStatus(Status.OVERDUE);

        overdueBorrows.forEach(Borrow::increaseOverdueDays); // 연체일 수 1일씩 증가

        log.info("연체 일 수 증가된 대출 {}건 업데이트", overdueBorrows.size());
    }
}
