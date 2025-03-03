package com.example.LibDev.reservation.controller;

import java.util.Collections;

import com.example.LibDev.auth.dto.CustomUserDetails;
import com.example.LibDev.reservation.dto.ReservationRequestDto;
import com.example.LibDev.reservation.dto.ReservationResponseDto;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationAPIController {

    private final ReservationService reservationService;

    // 예약 생성
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequestDto requestDto) {
        Reservation reservation = reservationService.createReservation(requestDto);
        return ResponseEntity.ok(reservation);
    }

    // 예약 내역 조회
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getUserReservations() {
        Long userId = getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<ReservationResponseDto> reservations = reservationService.getUserReservationsWithCanBorrow(userId);
        return ResponseEntity.ok(reservations);
    }

    // 전체 예약 조회
    @GetMapping("/admin")
    public ResponseEntity<Page< ReservationResponseDto>> getReservationList(
            @RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok(reservationService.getAllReservations(page));
    }

    // 예약 삭제
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        Long userId = getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }

    // 관리자가 예약 삭제
    @DeleteMapping("/admin/{reservationId}")
    public ResponseEntity<?> cancelReservationByAdmin(@PathVariable Long reservationId) {
        reservationService.cancelReservationByAdmin(reservationId);
        return ResponseEntity.ok("관리자가 예약을 취소하였습니다.");
    }

    @GetMapping("/count/{bookId}")
    public ResponseEntity<Integer> getReservationCount(@PathVariable Long bookId) {
        int count = reservationService.getReservationCountByBook(bookId);
        return ResponseEntity.ok(count);
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        return null;
    }
}