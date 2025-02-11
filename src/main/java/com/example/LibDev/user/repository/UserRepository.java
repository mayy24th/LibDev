package com.example.LibDev.user.repository;

import com.example.LibDev.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /*이메일로 회원 검색*/
    Optional<User> findByEmail(String email);

    /*로그인되어 있는 회원 검색*/
    User findLoginUserByEmail(String email);

}
