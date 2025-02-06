package com.example.LibDev.user.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN","관리자"),
    USER("ROLE_USER","정회원")
    ;

    private final String name;
    private final String value;
}
