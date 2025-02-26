package com.example.LibDev.user.mapper;

import com.example.LibDev.user.dto.BindingUserResDto;
import com.example.LibDev.user.dto.UserServiceCountResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    List<BindingUserResDto> findAllUsersWithOverdueDays(@Param("size") int size,
                                                        @Param("offset") int offset,
                                                        @Param("role") String role,
                                                        @Param("email") String email);

    int countAllUsers();

    int countAllUsersWithFilter(@Param("role") String role, @Param("email") String email);

    UserServiceCountResDto getUserServiceCount(@Param("email") String email);

}
