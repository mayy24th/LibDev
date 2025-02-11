package com.example.LibDev.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationRequestDto {
    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "bookId cannot be null")
    private Long bookId;
}
