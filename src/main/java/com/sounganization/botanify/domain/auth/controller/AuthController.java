package com.sounganization.botanify.domain.auth.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.auth.dto.req.SigninReqDto;
import com.sounganization.botanify.domain.auth.dto.req.SignupReqDto;
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

import java.util.Objects;

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
        CommonResDto commonResDto = authResDtoResponseEntity.getBody();

        // JWT 쿠키 생성
        try {
            String token = Objects.requireNonNull(commonResDto).token();
            Cookie jwtCookie = new Cookie("Authorization", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (authService.getExpirationTime()));
            response.addCookie(jwtCookie);
        } catch (NullPointerException ex) {
            throw new CustomException(ExceptionStatus.INVALID_TOKEN);
        }
        return ResponseEntity.ok(commonResDto);
    }
}
