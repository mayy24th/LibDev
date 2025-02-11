package com.example.LibDev.reservation.repository;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByUserAndBook(User user, Book book);
    List<Reservation> findByBookOrderByQueueOrderAsc(Book book);
}
