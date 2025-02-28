package com.example.LibDev.auth.dto;

import com.example.LibDev.user.entity.type.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class OAuth2UserDto {

    private final String email;
    private final String name;
    private final Role role;
}
