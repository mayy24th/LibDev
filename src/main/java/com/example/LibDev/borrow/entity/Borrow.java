package com.example.LibDev.borrow.entity;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.global.entity.BaseEntity;
import com.example.LibDev.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
public class Borrow extends BaseEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dueDate; // 반납 예정일

    private LocalDateTime returnDate; // 실제 반납일

    private boolean extended; // 연장 여부

    private boolean overdue; // 연체 여부

    private long overdueDays; // 연체일 수(초기값 0)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 대출 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void extendDuedate(LocalDateTime dueDate) {this.dueDate = dueDate;}
    public void updateExtended(boolean extended) {this.extended = extended;}
    public void updateOverdue(boolean overdue) {this.overdue = overdue;}
    public void updateOverdueDays(Long overdueDays) {this.overdueDays = overdueDays;}
    public void updateStatus(Status status) {this.status = status;}
    public void updateReturnDate(LocalDateTime returnDate) {this.returnDate = returnDate;}
}