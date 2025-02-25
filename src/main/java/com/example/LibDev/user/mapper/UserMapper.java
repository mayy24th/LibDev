package com.example.LibDev.user.mapper;

import com.example.LibDev.user.dto.UserResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserResDto> findAllUsersWithOverdueDays(@Param("size") int size, @Param("offset") int offset);

    int countAllUsers();
}
