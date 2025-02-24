package com.example.LibDev.auth.service;

import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.auth.dto.VerificationCodeDto;
import com.example.LibDev.auth.jwt.JwtProvider;
import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import com.example.LibDev.global.service.RedisCodeService;
import com.example.LibDev.global.service.RedisTokenService;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;
    private final VerificationMailService verificationMailService;
    private final RedisCodeService redisCodeService;
    private final PasswordEncoder passwordEncoder;

    /*토큰 생성*/
    public TokenResDto generateToken(String email, String role) {
        long accessTokenValidTime = jwtProvider.getAccessTokenValidTime();
        long refreshTokenValidTime = jwtProvider.getRefreshTokenValidTime();

        if(redisTokenService.getRefreshToken(email) != null){
            redisTokenService.delRefreshToken(email);
        }

        TokenResDto tokenResDto = TokenResDto.builder()
                .accessToken(jwtProvider.generateToken(email,role,"access-token",accessTokenValidTime))
                .refreshToken(jwtProvider.generateToken(email,role,"refresh-token",refreshTokenValidTime))
                .build();
        saveRefreshToken(email,tokenResDto.getRefreshToken());
        return tokenResDto;
    }

    /*리프레쉬 토큰 레디스에 저장*/
    public void saveRefreshToken(String email, String refreshToken) {
        redisTokenService.setRefreshToken(
                email,
                refreshToken,
                jwtProvider.getRefreshTokenValidTime()
        );
    }

    /*토큰 무효화*/
    public void deleteToken(String accessToken) {
        if (accessToken == null) {
            return;
        }

        String principal = getPrincipal(accessToken);
        long expiration = jwtProvider.getTokenValidTime(accessToken);

        String refreshTokenInReds = redisTokenService.getRefreshToken(principal);

        if(refreshTokenInReds != null){

            redisTokenService.delRefreshToken(principal);

            redisTokenService.setBlackList(accessToken,"logout",expiration - new Date().getTime());
        }
    }

    /*Principal 추출*/
    public String getPrincipal(String requestAccessToken) {
        return jwtProvider.getAuthentication(requestAccessToken).getName();
    }


    /*인증번호 전송*/
    public void sendVerificationCodeToEmail(String email) {
        log.info("sending verification code to email {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        String verificationCode = verificationMailService.createCode();

        if(redisCodeService.existCode(email)){
            redisCodeService.deleteCode(email);
        }

        long codeValidTime = 120 * 1000;
        redisCodeService.saveCode(email,verificationCode,codeValidTime);
        verificationMailService.sendVerificationMail(email,verificationCode);
    }

    /*인증번호 검증 후 비밀번호 재설정을 위한 임시 토큰 발급*/
    public String verifyCode(VerificationCodeDto verificationCodeDto) {
        String email = verificationCodeDto.getEmail();
        String verificationCode = verificationCodeDto.getVerificationCode();
        String verificationCodeInRedis = redisCodeService.getCode(email);

        if(!verificationCodeInRedis.equals(verificationCode)){
            throw new CustomException(CustomErrorCode.CODE_VERIFY_FAIL);
        }

        return jwtProvider.generateTempToken(email);
    }

    /*임시 토큰 발급 후 새 비밀번호 지정*/
    @Transactional
    public void resetPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        String newEncodePassword = passwordEncoder.encode(password);
        user.updatePassword(newEncodePassword);
    }



}
