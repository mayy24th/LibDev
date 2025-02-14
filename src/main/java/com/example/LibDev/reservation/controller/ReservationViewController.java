package com.example.LibDev.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationViewController {

    @GetMapping("/reservations/demo")
    public String showReservationDemo() {
        return "reservation/reservation-demo";
    }

    @GetMapping("/reservations/list")
    public String showReservationList() {
        return "reservation/reservationList";
    }


}
