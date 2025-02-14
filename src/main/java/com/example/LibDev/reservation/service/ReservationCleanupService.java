package com.example.LibDev.reservation.service;

import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupService {

    private final ReservationRepository reservationRepository;

    // 매일 자정(00:00)에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredReservations() {
        LocalDate today = LocalDate.now();
        List<Reservation> expiredReservations = reservationRepository.findByExpirationDateBefore(today);

        if (!expiredReservations.isEmpty()) {
            log.info("삭제할 예약 {}건 발견", expiredReservations.size());
            reservationRepository.deleteAll(expiredReservations);
        } else {
            log.info("만료된 예약 없음");
        }
    }
}
