package com.sounganization.botanify.domain.auth.controller;

import com.sounganization.botanify.domain.auth.dto.res.AuthResDto;
import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.auth.dto.req.AuthReqDto;
import com.sounganization.botanify.domain.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
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
    public ResponseEntity<CommonResDto> signup(@Valid @RequestBody SignupReqDto request) {
        return authService.signup(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<CommonResDto> signin(@Valid @RequestBody SigninReqDto request, HttpServletResponse response) {
        ResponseEntity<CommonResDto> authResDtoResponseEntity = authService.signin(request);
        AuthResDto authResDto = authResDtoResponseEntity.getBody();

        // JWT 쿠키 생성
        String token = authResDto.token();
        if (token != null) {
            Cookie jwtCookie = new Cookie("Authorization", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (authService.getExpirationTime()));
            response.addCookie(jwtCookie);
        }
        return ResponseEntity.ok(authResDto);
    }
}
