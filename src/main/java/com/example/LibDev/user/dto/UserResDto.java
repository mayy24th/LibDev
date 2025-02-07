package com.example.LibDev.user.dto;

import com.example.LibDev.user.entity.User;
import lombok.Getter;

@Getter
public class UserResDto {

    private String name;
    private String email;
    private String createdAt;

    public UserResDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt().toString();
    }

}
