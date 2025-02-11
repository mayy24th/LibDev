package com.example.LibDev.reservation.entity;

import com.example.LibDev.global.entity.BaseEntity;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDate reservedDate;

    private LocalDate expirationDate;

    @Column(nullable = false)
    private int queueOrder; // 대기 순번
}
