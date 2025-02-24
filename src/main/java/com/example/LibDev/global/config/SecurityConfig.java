package com.example.LibDev.global.config;

import com.example.LibDev.auth.filter.CustomAuthenticationFilter;
import com.example.LibDev.auth.filter.CustomLogoutFilter;
import com.example.LibDev.auth.handler.OAuth2LoginSuccessHandler;
import com.example.LibDev.auth.jwt.JwtFilter;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.auth.service.AuthService;
import com.example.LibDev.auth.service.CustomOauth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtProvider jwtProvider;
    private final AuthService authservice;
    private final ObjectMapper objectMapper;
    private final CustomOauth2UserService customOauth2UserService;



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/",
                                "/api/v1/auths/reissue",
                                "/api/v1/users",
                                "/api/v1/users/check-email/**",
                                "/reservations/**",
                                "/api/v1/reservations/**",
                                "/api/v1/auths/password-find/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(authservice, objectMapper), LogoutFilter.class)
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOauth2UserService))
                        .successHandler(new OAuth2LoginSuccessHandler(jwtProvider, authservice)))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
        ;
        return http.build();
    }

    @Bean
    public CustomAuthenticationFilter authenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(authenticationConfiguration)
                ,objectMapper,authservice,jwtProvider );

        //로그인 필터 url 설정
        customAuthenticationFilter.setFilterProcessesUrl("/api/v3/auths/login");
        customAuthenticationFilter.afterPropertiesSet();

        return customAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtProvider,objectMapper);
    }
}
