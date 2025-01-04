package com.sounganization.botanify.common.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class EmailVerificationRedisConfig {

    @Value("${spring.redis.verification.ttl:300}")
    private long verificationTtl;

    @Value("${spring.redis.verification.max-attempts:5}")
    private int maxAttempts;

    @Value("${spring.redis.verification.attempts-ttl:3600}")
    private long attemptsTtl;

    @Bean
    public RedisTemplate<String, String> emailVerificationRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        RedisTemplate<String, String> emailTemplate = new RedisTemplate<>();
        emailTemplate.setConnectionFactory(redisTemplate.getConnectionFactory());
        emailTemplate.setKeySerializer(new StringRedisSerializer());
        emailTemplate.setValueSerializer(new StringRedisSerializer());
        emailTemplate.setHashKeySerializer(new StringRedisSerializer());
        emailTemplate.setHashValueSerializer(new StringRedisSerializer());
        emailTemplate.afterPropertiesSet();
        return emailTemplate;
    }

    @Bean
    public EmailVerificationProperties emailVerificationProperties() {
        return new EmailVerificationProperties(verificationTtl, maxAttempts, attemptsTtl);
    }
}
