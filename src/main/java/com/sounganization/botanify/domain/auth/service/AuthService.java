package com.sounganization.botanify.domain.auth.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.util.JwtUtil;
import com.sounganization.botanify.domain.auth.dto.req.SigninReqDto;
import com.sounganization.botanify.domain.auth.dto.req.SignupReqDto;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import com.sounganization.botanify.domain.weather.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final LocationService locationService;

    public ResponseEntity<CommonResDto> signup(SignupReqDto request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomException(ExceptionStatus.DUPLICATED_EMAIL);
        }

        if (!request.password().equals(request.passwordCheck())) {
            throw new CustomException(ExceptionStatus.PASSWORDS_DO_NOT_MATCH);
        }

        String[] coordinates = locationService.getCoordinates(request.city(), request.town());
        if (coordinates == null) {
            throw new CustomException(ExceptionStatus.INVALID_COORDINATES);
        }

        String nx = coordinates[0];
        String ny = coordinates[1];

        UserRole role = request.role() != null ? request.role() : UserRole.USER; // 기본값 USER

        User newUser = User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .city(request.city())
                .town(request.town())
                .address(request.address())
                .role(role)
                .nx(nx)
                .ny(ny)
                .build();

        Long userId = userRepository.save(newUser).getId();

        CommonResDto response = new CommonResDto(HttpStatus.CREATED, "회원가입이 성공되었습니다.", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<CommonResDto> signin(SigninReqDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_DETAILS_NOT_FOUND));

        if (user.getDeletedYn()) {
            throw new CustomException(ExceptionStatus.ACCOUNT_DELETED);
        }
        // Spring Security 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = jwtUtil.generateToken(authentication);

        CommonResDto response = new CommonResDto(HttpStatus.OK, "로그인이 성공되었습니다.", token);
        return ResponseEntity.ok(response);
    }

    public long getExpirationTime() {
        return jwtUtil.getExpirationTime();
    }
}
