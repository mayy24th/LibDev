package com.example.LibDev.reservation.repository;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByUserAndBook(User user, Book book);
    boolean existsByBookAndStatus(Book book, ReservationStatus status);
    List<Reservation> findByBookOrderByQueueOrderAsc(Book book);
    List<Reservation> findByUser(User user);

    // 특정 책의 예약 개수 조회
    int countByBookAndStatus(Book book, ReservationStatus status);

    // 사용자의 예약 개수 조회
    int countByUserAndStatus(User user, ReservationStatus status);

    // 만료된 예약 조회
    List<Reservation> findByExpirationDateBefore(LocalDate date);
}