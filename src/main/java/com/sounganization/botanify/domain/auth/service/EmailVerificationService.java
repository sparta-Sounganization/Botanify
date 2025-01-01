package com.sounganization.botanify.domain.auth.service;

import com.sounganization.botanify.common.config.redis.EmailVerificationProperties;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private static final String EMAIL_VERIFICATION_PREFIX = "email:verification:";
    private static final String EMAIL_VERIFIED_PREFIX = "email:verified:";
    private static final String EMAIL_ATTEMPTS_PREFIX = "email:attempts:";
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int VERIFICATION_CODE_BOUND = 1000000; // 10^6

    private final RedisTemplate<String, String> emailVerificationRedisTemplate;
    private final EmailService emailService;
    private final EmailVerificationProperties properties;

    public void sendVerificationCode(String email) {
        try {
            if (!canSendVerificationCode(email)) {
                throw new CustomException(ExceptionStatus.VERIFICATION_CODE_RECENTLY_SENT);
            }

            String verificationCode = generateVerificationCode();
            String redisKey = EMAIL_VERIFICATION_PREFIX + email;

            emailVerificationRedisTemplate.opsForValue().set(
                    redisKey,
                    verificationCode,
                    properties.getVerificationTtl(),
                    TimeUnit.SECONDS
            );

            emailService.sendVerificationEmail(email, verificationCode);
        } catch (Exception e) {
            throw new CustomException(ExceptionStatus.EMAIL_VERIFICATION_FAILED);
        }
    }

    public boolean isEmailVerified(String email) {
        String redisKey = EMAIL_VERIFIED_PREFIX + email;
        String verificationStatus = emailVerificationRedisTemplate.opsForValue().get(redisKey);
        return Boolean.TRUE.toString().equals(verificationStatus);
    }

    public boolean verifyCode(String email, String code) {
        String attemptsKey = EMAIL_ATTEMPTS_PREFIX + email;
        String attempts = emailVerificationRedisTemplate.opsForValue().get(attemptsKey);
        int currentAttempts = attempts != null ? Integer.parseInt(attempts) : 0;

        if (currentAttempts >= properties.getMaxAttempts()) {
            throw new CustomException(ExceptionStatus.MAX_VERIFICATION_ATTEMPTS_EXCEEDED);
        }

        String redisKey = EMAIL_VERIFICATION_PREFIX + email;
        String storedCode = emailVerificationRedisTemplate.opsForValue().get(redisKey);

        if (storedCode != null && storedCode.equals(code)) {
            markEmailAsVerified(email);
            emailVerificationRedisTemplate.delete(attemptsKey);
            return true;
        }

        emailVerificationRedisTemplate.opsForValue().set(
                attemptsKey,
                String.valueOf(currentAttempts + 1),
                properties.getAttemptsTtl(),
                TimeUnit.SECONDS
        );
        return false;
    }

    private void markEmailAsVerified(String email) {
        String redisKey = EMAIL_VERIFIED_PREFIX + email;
        emailVerificationRedisTemplate.opsForValue().set(
                redisKey,
                Boolean.TRUE.toString(),
                properties.getVerificationTtl(),
                TimeUnit.SECONDS
        );
    }

    public boolean canSendVerificationCode(String email) {
        String redisKey = EMAIL_VERIFICATION_PREFIX + email;
        return emailVerificationRedisTemplate.opsForValue().get(redisKey) == null;
    }

    public void clearVerification(String email) {
        String verificationKey = EMAIL_VERIFICATION_PREFIX + email;
        String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
        String attemptsKey = EMAIL_ATTEMPTS_PREFIX + email;

        emailVerificationRedisTemplate.delete(verificationKey);
        emailVerificationRedisTemplate.delete(verifiedKey);
        emailVerificationRedisTemplate.delete(attemptsKey);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(VERIFICATION_CODE_BOUND);
        return String.format("%0" + VERIFICATION_CODE_LENGTH + "d", code);
    }
}
