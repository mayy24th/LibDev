package com.example.LibDev.reservation.entity;

import com.example.LibDev.global.entity.BaseEntity;
import com.example.LibDev.reservation.entity.type.ReservationStatus;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime reservedDate;

    private LocalDateTime expirationDate;

    public void updateExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void updateQueueOrder(int newQueueOrder) {
        this.queueOrder = newQueueOrder;
    }

    @Column(nullable = false)
    private int queueOrder;

}
