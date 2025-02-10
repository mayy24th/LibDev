package com.example.LibDev.user.service;

import com.example.LibDev.user.dto.JoinReqDto;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
                .role(Role.USER)
                .build();

        User savedUser =  userRepository.save(user);

        return new UserResDto(savedUser);
    }
}
