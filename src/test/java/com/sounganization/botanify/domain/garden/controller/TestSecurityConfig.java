package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.common.handler.JwtAuthorizationHandler;
import com.sounganization.botanify.common.util.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public JwtUtil jwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        jwtUtil.configure("testSecretKey", 3600000L); // 테스트용 키와 만료 시간 설정
        return jwtUtil;
    }

    @Bean
    public JwtAuthorizationHandler jwtAuthorizationHandler(JwtUtil jwtUtil) {
        return new JwtAuthorizationHandler(jwtUtil);
    }
}