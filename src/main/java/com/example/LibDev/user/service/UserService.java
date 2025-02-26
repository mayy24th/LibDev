package com.example.LibDev.user.service;

import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.auth.service.AuthService;
import com.example.LibDev.borrow.entity.type.Status;
import com.example.LibDev.borrow.repository.BorrowRepository;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.reservation.entity.Reservation;
import com.example.LibDev.reservation.repository.ReservationRepository;
import com.example.LibDev.reservation.service.ReservationService;
import com.example.LibDev.user.dto.JoinReqDto;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.dto.UserServiceCountResDto;
import com.example.LibDev.user.dto.UserUpdateReqDto;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import com.example.LibDev.user.mapper.UserMapper;
import com.example.LibDev.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final UserMapper userMapper;

    public UserResDto join(JoinReqDto joinReqDto) {

        User user = User.builder()
                .email(joinReqDto.getEmail())
                .password(passwordEncoder.encode(joinReqDto.getPassword()))
                .name(joinReqDto.getName())
                .phone(joinReqDto.getPhone())
                .borrowAvailable(true)
                .penaltyExpiration(null)
                .withdraw(false)
                .role(Role.USER)
                .build();

        User savedUser =  userRepository.save(user);

        return UserResDto.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }

    public UserResDto info() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.AUTHENTICATION_REQUIRED));
        return UserResDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /*회원 정보 수정*/
    @Transactional
    public UserResDto update(UserUpdateReqDto userUpdateReqDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findLoginUserByEmail(email);

        user.update(
                userUpdateReqDto.getEmail(),
                userUpdateReqDto.getName(),
                userUpdateReqDto.getPhone()
        );

        return UserResDto.builder().
                name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

    }
    
    /*회원 비밀번호 변경*/
    @Transactional
    public void updatePassword(UserUpdateReqDto userUpdateReqDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findLoginUserByEmail(email);
        String newEncodePassword = passwordEncoder.encode(userUpdateReqDto.getPassword());
        user.updatePassword(newEncodePassword);
    }

    /*이메일 중복 체크*/
    public void checkEmailDuplication(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new CustomException(CustomErrorCode.DUPLICATE_EMAIL);
        }
    }

    /*회원 탈퇴*/
    @Transactional
    public void deleteUser(HttpServletRequest request) {

        String accessToken = jwtProvider.resolveTokenInCookie(request);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findLoginUserByEmail(email);

        if(borrowRepository.countByUserAndStatusNot(user, Status.RETURNED)>0){
            throw new CustomException(CustomErrorCode.USER_HAS_ACTIVE_BORROWS);
        }

        List<Reservation> reservations = reservationRepository.findByUser(user);

        if(!reservations.isEmpty()){
            for(Reservation reservation : reservations){
                reservationService.cancelReservation(user.getId(), reservation.getId());
            }
        }

        authService.deleteToken(accessToken);
        user.deleteUser();
    }

    /*회원 서비스 이력 현황 카운트*/
    public UserServiceCountResDto getUserServiceCount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.getUserServiceCount(email);
    }

    /*이메일 반환*/
    public String getUserEmail(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(Objects.equals(email, "anonymousUser")){
            return null;
        } else{
            return email;
        }
    }

    /* userId 반환*/
    public Long getUserId() {
        String email = getUserEmail();
        if (email == null) {
            return null;
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        return user.getId();
    }

}
