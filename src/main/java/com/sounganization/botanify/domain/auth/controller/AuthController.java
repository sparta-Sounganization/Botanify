package com.sounganization.botanify.domain.auth.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.auth.dto.req.EmailVerificationCodeReqDto;
import com.sounganization.botanify.domain.auth.dto.req.EmailVerificationReqDto;
import com.sounganization.botanify.domain.auth.dto.req.SigninReqDto;
import com.sounganization.botanify.domain.auth.dto.req.SignupReqDto;
import com.sounganization.botanify.domain.auth.service.AuthService;
import com.sounganization.botanify.domain.auth.service.EmailVerificationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResDto> signup(@Valid @RequestBody SignupReqDto request) {
        return authService.signup(request);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<CommonResDto> sendVerificationEmail(@Valid @RequestBody EmailVerificationReqDto request) {
        emailVerificationService.sendVerificationCode(request.email());
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "인증 코드가 전송되었습니다."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<CommonResDto> verifyCode(@Valid @RequestBody EmailVerificationCodeReqDto request) {
        boolean isValid = emailVerificationService.verifyCode(request.email(), request.code());
        if (!isValid) {
            throw new CustomException(ExceptionStatus.INVALID_VERIFICATION_CODE);
        }
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "이메일 인증이 완료되었습니다."));
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
