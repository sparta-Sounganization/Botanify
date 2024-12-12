package com.sounganization.botanify.domain.auth.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.auth.dto.req.AuthReqDto;
import com.sounganization.botanify.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResDto> signup(@RequestBody AuthReqDto request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResDto> login(@RequestBody AuthReqDto request) {
        return authService.login(request);
    }
}
