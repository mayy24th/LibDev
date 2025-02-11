package com.example.LibDev.user.service;

import com.example.LibDev.user.dto.JoinReqDto;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                .borrow_available(true)
                .penalty_expiration(null)
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
        User user = userRepository.findLoginUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return UserResDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
