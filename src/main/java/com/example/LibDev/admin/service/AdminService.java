package com.example.LibDev.admin.service;

import com.example.LibDev.user.dto.UserResDto;
import com.example.LibDev.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final static int defaultSize = 10;
    private final UserMapper userMapper;

    public Page<UserResDto> findAllUsers(int page) {
        int offset = page * defaultSize;

        List<UserResDto> users = userMapper.findAllUsersWithOverdueDays(defaultSize, offset);

        int totalCount = userMapper.countAllUsers();

        return new PageImpl<>(users, PageRequest.of(page, defaultSize), totalCount);
    }
}
