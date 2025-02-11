package com.example.LibDev.reservation.controller;

import com.example.LibDev.reservation.dto.ReservationRequestDto;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationAPIController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequestDto requestDto) {
        Reservation reservation = reservationService.createReservation(requestDto);
        return ResponseEntity.ok(reservation);
    }
}