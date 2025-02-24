package com.example.LibDev.auth.service;

import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import org.thymeleaf.context.Context;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationMailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    /*6자리 난수 생성*/
    public String createCode(){
        Random rand = new Random();
        StringBuilder verificationCode = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            verificationCode.append(rand.nextInt(10));
        }

        return verificationCode.toString();
    }

    /*이메일로 코드 전송*/
    public void sendVerificationMail(String email, String verificationCode){
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            mimeMessage.addRecipients(MimeMessage.RecipientType.TO, email);
            mimeMessage.setSubject("[Libdev] 인증 번호 발송드립니다");
            mimeMessage.setText(setVerificationCodeContext(verificationCode),"utf-8","html");

            mimeMessage.setFrom(new InternetAddress(username + host,"Libdev"));
            mailSender.send(mimeMessage);
        } catch (Exception e){
            throw new CustomException(CustomErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /*이메일 본문에 코드 삽입*/
    private String setVerificationCodeContext(String verificationCode){
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        return templateEngine.process("user/verification-code-mail", context);
    }
}
