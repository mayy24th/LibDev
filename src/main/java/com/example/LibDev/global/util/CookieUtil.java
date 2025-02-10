package com.example.LibDev.global.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie createCookie(String cookieName, String cookieValue, long maxAge) {
        return ResponseCookie.from(cookieName, cookieValue)
                .maxAge(maxAge)
                .path("/")
                .sameSite("Strict")
                .httpOnly(true)
                .secure(true)
                .build();
    }
}
