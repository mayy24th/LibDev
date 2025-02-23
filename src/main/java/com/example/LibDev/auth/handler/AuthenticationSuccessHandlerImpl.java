package com.example.LibDev.auth.handler;

import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.auth.service.AuthService;
import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.global.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    private final AuthService authService;
    private final ObjectMapper mapper;

    private static final String ACCESS_TOKEN_HEADER = "access-token";
    private static final String REFRESH_COOKIE_HEADER = "refresh-token";
    private final JwtProvider jwtProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenResDto tokenResDto = authService.generateToken(authentication);

        response.addHeader(HttpHeaders.SET_COOKIE,CookieUtil.createCookie(ACCESS_TOKEN_HEADER,tokenResDto.getAccessToken(), jwtProvider.getAccessTokenValidTime()).toString() );
        response.addHeader(HttpHeaders.SET_COOKIE,CookieUtil.createCookie(REFRESH_COOKIE_HEADER,tokenResDto.getRefreshToken(),jwtProvider.getRefreshTokenValidTime()).toString());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(GlobalResponseDto.success(HttpStatus.OK,"로그인 성공")));
    }


}
