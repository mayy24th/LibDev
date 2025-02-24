package com.example.LibDev.auth.handler;

import com.example.LibDev.auth.dto.CustomOAuth2User;
import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.auth.service.AuthService;
import com.example.LibDev.global.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final AuthService authService;

    private static final String ACCESS_TOKEN_HEADER = "access-token";
    private static final String REFRESH_COOKIE_HEADER = "refresh-token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();



        String email = customOAuth2User.getEmail();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        TokenResDto tokenResDto = authService.generateToken(email, role);

        String accessToken = tokenResDto.getAccessToken();
        String refreshToken = tokenResDto.getRefreshToken();

        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(ACCESS_TOKEN_HEADER,accessToken, jwtProvider.getAccessTokenValidTime()).toString() );
        response.addHeader(HttpHeaders.SET_COOKIE,CookieUtil.createCookie(REFRESH_COOKIE_HEADER,refreshToken,jwtProvider.getRefreshTokenValidTime()).toString());

        response.sendRedirect("/");

    }
}
