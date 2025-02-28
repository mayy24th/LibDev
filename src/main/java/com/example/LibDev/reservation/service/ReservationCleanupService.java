package com.example.LibDev.reservation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupService {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final UserRepository userRepository;

    @Transactional
    public void checkAndDeleteExpiredReservations() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getPenaltyExpiration() != null) {
                log.info("사용자 '{}'의 패널티가 적용되어 있어 모든 예약을 삭제합니다.", user.getEmail());
                reservationService.deleteAllReservationsForUser(user);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredReservations() {
        try {
            checkAndDeleteExpiredReservations();

            LocalDateTime today = LocalDateTime.now();
            List<Reservation> expiredReservations = reservationRepository.findByExpirationDateBefore(today);

            if (!expiredReservations.isEmpty()) {
                // 영향을 받은 book ID 수집
                Set<Book> affectedBooks = expiredReservations.stream()
                        .map(Reservation::getBook)
                        .collect(Collectors.toSet());

                reservationRepository.deleteAll(expiredReservations);

                // 삭제된 예약이 있는 도서별로 예약 대기열 재정렬
                for (Book book : affectedBooks) {
                    List<Reservation> remainingReservations = reservationRepository.findByBookOrderByQueueOrderAsc(book);

                    // queue_order를 1부터 다시 부여
                    for (int i = 0; i < remainingReservations.size(); i++) {
                        remainingReservations.get(i).updateQueueOrder(i + 1);
                        reservationRepository.save(remainingReservations.get(i));
                    }
                }

                // 삭제된 예약 도서의 다음 예약자 만료일 업데이트
                for (Book book : affectedBooks) {
                    reservationService.updateFirstReservationExpiration(book);
                }
            }
        } catch (Exception e) {
            log.error("만료된 예약 삭제 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
