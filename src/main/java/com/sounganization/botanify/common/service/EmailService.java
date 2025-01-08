package com.sounganization.botanify.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Botanify 이메일 인증");
        message.setText("인증 코드: " + verificationCode + "\n\n이 코드를 입력하여 이메일 인증을 완료해주세요.");
        mailSender.send(message);
    }
}
