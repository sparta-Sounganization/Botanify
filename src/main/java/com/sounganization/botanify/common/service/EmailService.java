package com.sounganization.botanify.common.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String to, String verificationCode) {
        try {
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(to))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withText(new Content()
                                            .withCharset("UTF-8")
                                            .withData("인증 코드: " + verificationCode + "\n\n이 코드를 입력하여 이메일 인증을 완료해주세요.")))
                            .withSubject(new Content()
                                    .withCharset("UTF-8")
                                    .withData("Botanify 이메일 인증")))
                    .withSource(fromEmail);

            amazonSimpleEmailService.sendEmail(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
