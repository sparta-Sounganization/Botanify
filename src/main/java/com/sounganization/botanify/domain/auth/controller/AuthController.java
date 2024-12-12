package com.sounganization.botanify.domain.auth.controller;

import com.sounganization.botanify.domain.auth.dto.req.SigninReqDto;
import com.sounganization.botanify.domain.auth.dto.req.SignupReqDto;
import com.sounganization.botanify.domain.auth.dto.res.AuthResDto;
import com.sounganization.botanify.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<AuthResDto> signup(@Valid @RequestBody SignupReqDto request) {
        return authService.signup(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResDto> signin(@Valid @RequestBody SigninReqDto request, HttpServletResponse response) {
        return authService.signin(request, response);
    }
}
