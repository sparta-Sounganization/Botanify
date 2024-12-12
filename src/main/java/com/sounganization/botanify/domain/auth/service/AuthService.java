package com.sounganization.botanify.domain.auth.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.auth.dto.req.AuthReqDto;
import com.sounganization.botanify.domain.user.dto.req.UserReqDto;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<CommonResDto> signup(AuthReqDto request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomException(ExceptionStatus.DUPLICATED_EMAIL);
        }

        if (!request.password().equals(request.passwordCheck())) {
            throw new CustomException(ExceptionStatus.PASSWORDS_DO_NOT_MATCH);
        }

        UserRole role = request.role() != null ? request.role() : UserRole.USER; // 기본값 USER

        UserReqDto userReqDto = new UserReqDto(
                null,
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.city(),
                request.town(),
                request.address(),
                role
        );

        Long userId = userRepository.saveNewUser(userReqDto).getId();

        CommonResDto response = new CommonResDto(HttpStatus.CREATED, "회원가입이 성공되었습니다.", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<CommonResDto> login(AuthReqDto request) {
        UserReqDto userReqDto = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ExceptionStatus.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), userReqDto.password())) {
            throw new CustomException(ExceptionStatus.INVALID_CREDENTIALS);
        }

        // Spring Security 인증 처리
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 로그인 성공 응답
        CommonResDto authResDto = new CommonResDto(HttpStatus.OK, "로그인이 성공하였습니다.");
        return ResponseEntity.ok(authResDto);
    }
}
