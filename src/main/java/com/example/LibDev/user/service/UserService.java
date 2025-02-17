package com.example.LibDev.user.service;

import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.user.dto.JoinReqDto;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.dto.UserUpdateReqDto;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        User user = userRepository.findLoginUserByEmail(email);
        return UserResDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
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

}
