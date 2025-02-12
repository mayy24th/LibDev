package com.example.LibDev.borrow.repository;

import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.entity.type.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    int countByUserIdAndStatus(Long userId, Status status);
}
