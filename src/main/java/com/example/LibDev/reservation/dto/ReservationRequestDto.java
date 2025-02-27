package com.example.LibDev.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservationRequestDto {
    @NotNull(message = "userId null 불가")
    private Long userId;

    @NotNull(message = "bookId null 불가")
    private Long bookId;

    @Email(message = "유효하지않은 email format")
    @NotNull(message = "Email null 불가")
    private String email;
}