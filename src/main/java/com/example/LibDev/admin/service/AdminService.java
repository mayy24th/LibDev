package com.example.LibDev.admin.service;

import com.example.LibDev.admin.dto.UpdateRoleDto;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.user.dto.BindingUserResDto;
import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import com.example.LibDev.user.mapper.UserMapper;

import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final static int defaultSize = 10;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public Page<BindingUserResDto> findAllUsers(int page, String role, String email) {

        int offset = page * defaultSize;

        List<BindingUserResDto> users = userMapper.findAllUsersWithOverdueDays(defaultSize, offset, role, email);

        int totalCount = userMapper.countAllUsersWithFilter(role, email);

        return new PageImpl<>(users, PageRequest.of(page, defaultSize), totalCount);
    }

    @Transactional
    public void changeRole(UpdateRoleDto updateRoleDto) {

        String email = updateRoleDto.getEmail();
        String role = updateRoleDto.getRole();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if(("USER".equals(role))){
            user.updateRole(Role.USER);
        } else if(("ADMIN".equals(role))){
            user.updateRole(Role.ADMIN);
        } else {
            log.error("잘못된 role 할당 {}",role);
            throw new CustomException(CustomErrorCode.DATA_INTEGRITY_VIOLATION);
        }
    }
}
