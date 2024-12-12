package com.sounganization.botanify.domain.auth.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.util.JwtUtil;
import com.sounganization.botanify.domain.auth.dto.req.SigninReqDto;
import com.sounganization.botanify.domain.auth.dto.req.SignupReqDto;
import com.sounganization.botanify.domain.auth.dto.res.AuthResDto;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ResponseEntity<AuthResDto> signup(SignupReqDto request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomException(ExceptionStatus.DUPLICATED_EMAIL);
        }

        if (!request.password().equals(request.passwordCheck())) {
            throw new CustomException(ExceptionStatus.PASSWORDS_DO_NOT_MATCH);
        }

        UserRole role = request.role() != null ? request.role() : UserRole.USER; // 기본값 USER

        User newUser = User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .city(request.city())
                .town(request.town())
                .address(request.address())
                .role(role)
                .build();

        Long userId = userRepository.save(newUser).getId();

        AuthResDto response = new AuthResDto(201, "회원가입이 성공되었습니다.", userId);
        return ResponseEntity.status(201).body(response);
    }

    public ResponseEntity<AuthResDto> signin(SigninReqDto request, HttpServletResponse response) {
        // Spring Security 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = jwtUtil.generateToken(authentication);

        Cookie jwtCookie = new Cookie("Authorization", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) (jwtUtil.getExpirationTime()));

        response.addCookie(jwtCookie);

        // 로그인 성공 응답
        AuthResDto authResDto = new AuthResDto(200, "로그인이 성공하였습니다.");
        return ResponseEntity.ok(authResDto);
    }
}
