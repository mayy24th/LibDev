package com.example.LibDev.borrow.repository;

import com.example.LibDev.book.entity.Book;
import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    int countByUserAndStatusNot(User user, Status status);
    Page<Borrow> findByUserAndStatus(User user, Status status, Pageable pageable);
    Page<Borrow> findByStatus(Status status, Pageable pageable);
    List<Borrow> findByUserAndStatusNotOrderByIdDesc(User user, Status status);
    List<Borrow> findByStatusAndDueDateBefore(Status status, LocalDateTime dueDate);

    Boolean existsByUser(User user);

    // 특정사용자의 반납완료가 아닌 대출 여부를 확인하는 메서드
    boolean existsByUserAndBookAndStatusNot(User user, Book book, Status status);

    boolean existsByBookAndStatusNot(Book book, Status status);

}
