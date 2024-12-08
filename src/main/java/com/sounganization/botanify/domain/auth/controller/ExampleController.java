package com.sounganization.botanify.domain.auth.controller;

import com.sounganization.botanify.common.handler.JwtAuthorizationHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class ExampleController {

    private final JwtAuthorizationHandler jwtAuthorizationHandler;

    @GetMapping("/secure-data")
    public ResponseEntity<String> getSecureData(HttpServletRequest request) {
        jwtAuthorizationHandler.handleAuthorization(request);
        return ResponseEntity.ok("인증된 사용자만 접근할 수 있는 데이터입니다.");
    }
}
