package com.example.LibDev.auth.filter;

import com.example.LibDev.auth.dto.LoginReqDto;
import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.auth.service.AuthService;
import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.global.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    private static final String ACCESS_TOKEN_HEADER = "access-token";
    private static final String REFRESH_COOKIE_HEADER = "refresh-token";


    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, AuthService authService, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.authService = authService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final UsernamePasswordAuthenticationToken authRequest;
        final LoginReqDto loginReqDto;

        try{
            loginReqDto = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);
            authRequest = new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword());

        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        handleSuccessAuthentication(response, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        handleFailureAuthentication(response, exception);
    }

    private void handleSuccessAuthentication(HttpServletResponse response, Authentication authentication) throws IOException {
        TokenResDto tokenResDto = authService.generateToken(authentication);

        String accessToken = tokenResDto.getAccessToken();
        String refreshToken = tokenResDto.getRefreshToken();

        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(ACCESS_TOKEN_HEADER,accessToken, jwtProvider.getAccessTokenValidTime()).toString() );
        response.addHeader(HttpHeaders.SET_COOKIE,CookieUtil.createCookie(REFRESH_COOKIE_HEADER,refreshToken,jwtProvider.getRefreshTokenValidTime()).toString());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(GlobalResponseDto.success(HttpStatus.OK,"로그인 성공")));

    }

    private void handleFailureAuthentication(HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                GlobalResponseDto.fail(HttpStatus.UNAUTHORIZED, exception.getMessage())
        ));
    }
}
