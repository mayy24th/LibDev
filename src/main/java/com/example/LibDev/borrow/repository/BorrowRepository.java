package com.example.LibDev.borrow.repository;

import com.example.LibDev.borrow.entity.Borrow;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    int countByUserAndStatus(User user, Status status);
    List<Borrow> findByUserAndStatusNot(User user, Status status);

    Boolean existsByUser(User user);
}
