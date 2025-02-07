package com.example.LibDev.reservation.service;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.reservation.dto.ReservationRequestDto;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    //예약생성
    public Reservation createReservation(ReservationRequestDto requestDto) {
        //user 객체 조회
        User user = userRepository.findById(requestDto.getUserId()).orElse(null);
        Book book = null;

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .status(ReservationStatus.WAITING)
                .reservedDate(LocalDate.now())
                .queueOrder(getNextQueueOrder(book))
                .build();

        return reservationRepository.save(reservation);
    }

    //예약 대기 순번 계산
    private int getNextQueueOrder(Book book) {
        return reservationRepository.findByBookOrderByQueueOrderAsc(book).size() + 1;
    }
}