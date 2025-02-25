package com.example.LibDev.user.repository;

import com.example.LibDev.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    /*이메일로 회원 검색*/
    Optional<User> findByEmail(String email);

    /*로그인되어 있는 회원 검색*/
    User findLoginUserByEmail(String email);

    /*이메일이 존재하는 검색*/
    boolean existsByEmail(String email);

    /*패널티 만료 여부 조회*/
    List<User> findByPenaltyExpirationBeforeAndBorrowAvailableFalse(LocalDateTime now);

    /*미탈퇴 회원 목록 조회*/
    Page<User> findUsersByWithdrawFalse(Pageable pageable);

}
