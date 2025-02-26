package com.example.LibDev.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class ReservationViewController {

    @GetMapping("/reservations/list")
    public String showReservationList() {
        return "reservation/reservation-list";
    }

    @GetMapping("/admin/reservations")
    public String reservationList() {
        return "reservation/admin-reservation-manage";
    }
}
