package com.example.LibDev.auth.filter;

import com.example.LibDev.auth.service.AuthService;
import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.global.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_COOKIE_HEADER = "refresh-token";
    private static final long REFRESH_TOKEN_DEL = 0;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        String logoutURI = "/api/v1/auths/logout";
        String logoutMethod = "POST";

        if(!requestURI.equals(logoutURI) || !requestMethod.equals(logoutMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader(AUTHORIZATION_HEADER);

        authService.deleteToken(accessToken);

        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(REFRESH_COOKIE_HEADER,null,REFRESH_TOKEN_DEL).toString());
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(objectMapper.writeValueAsString(
                GlobalResponseDto.success(HttpStatus.OK,"로그아웃 성공")
        ));
    }
}
