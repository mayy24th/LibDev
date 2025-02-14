package com.example.LibDev.reservation.dto;

import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReservationResponseDto {
    private Long reservationId;
    private String bookTitle;
    private String author;
    private ReservationStatus status;
    private LocalDate reservedDate;
    private LocalDate expirationDate;
    private int queueOrder;

    public static ReservationResponseDto fromEntity(Reservation reservation) {
        if (reservation == null || reservation.getBook() == null) {
            throw new IllegalArgumentException("Reservation or Book cannot be null");
        }

        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getBook().getTitle(),
                reservation.getBook().getAuthor(),
                reservation.getStatus(),
                reservation.getReservedDate(),
                reservation.getExpirationDate(),
                reservation.getQueueOrder()
        );
    }
}

