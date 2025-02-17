package com.example.LibDev.reservation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.review.repository.ReviewRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupService {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final UserRepository userRepository;

    /*@Autowired
    public ReservationCleanupService(ReservationRepository reservationRepository,
                                     ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }*/

    @Transactional
    public void checkAndDeleteExpiredReservations() {
        List<User> users = userRepository.findAll(); // 모든 사용자 가져오기

        for (User user : users) {
            if (user.getPenaltyExpiration() != null) {
                log.info("사용자 '{}'의 패널티가 적용되어 있어 모든 예약을 삭제합니다.", user.getEmail());
                reservationService.deleteAllReservationsForUser(user);
            }
        }

        log.info("패널티 확인 및 예약 삭제 작업 완료.");
    }

    // 매일 자정(00:00)에 실행
    @Scheduled(cron = "0 0 9-18 * * ?")
    @Transactional
    public void deleteExpiredReservations() {
        try {
            checkAndDeleteExpiredReservations();

            LocalDateTime today = LocalDateTime.now();
            List<Reservation> expiredReservations = reservationRepository.findByExpirationDateBefore(today);

            if (!expiredReservations.isEmpty()) {
                log.info("삭제할 예약 {}건 발견", expiredReservations.size());

                // 영향을 받은 book ID 수집
                Set<Book> affectedBooks = expiredReservations.stream()
                        .map(Reservation::getBook)
                        .collect(Collectors.toSet());

                reservationRepository.deleteAll(expiredReservations);

                // 삭제된 예약 도서의 다음 예약자 만료일 업데이트
                for (Book book : affectedBooks) {
                    reservationService.updateFirstReservationExpiration(book);
                }

                log.info("만료된 예약 삭제 완료 및 {}개의 책에 대해 다음 예약자 만료일 업데이트 완료", affectedBooks.size());
            } else {
                log.info("만료된 예약 없음");
            }
        } catch (Exception e) {
            log.error("만료된 예약 삭제 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
