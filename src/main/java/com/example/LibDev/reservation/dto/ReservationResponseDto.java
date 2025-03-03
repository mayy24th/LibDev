package com.example.LibDev.reservation.dto;

import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponseDto {
    private Long reservationId;
    private Long bookId;
    private String bookTitle;
    private String author;
    private String userEmail;
    private ReservationStatus status;
    private LocalDateTime reservedDate;
    private LocalDateTime expirationDate;
    private int queueOrder;
    private int totalQueueSize;
    private boolean canBorrow;

    public static ReservationResponseDto fromEntity(Reservation reservation, int totalQueueSize, boolean canBorrow) {
        if (reservation == null || reservation.getBook() == null) {
            throw new IllegalArgumentException("Reservation or Book cannot be null");
        }

        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getBook().getBookId(),
                reservation.getBook().getTitle(),
                reservation.getBook().getAuthor(),
                reservation.getUser().getEmail(),
                reservation.getStatus(),
                reservation.getReservedDate(),
                reservation.getExpirationDate(),
                reservation.getQueueOrder(),
                totalQueueSize,
                canBorrow
        );
    }
}
