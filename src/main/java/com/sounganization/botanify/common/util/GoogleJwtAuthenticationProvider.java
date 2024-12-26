package com.sounganization.botanify.common.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleJwtAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final GoogleJwtValidator googleJwtValidator;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        // GoogleJwt 에서 사용자 정보 추출
        DecodedJWT jwt = googleJwtValidator.validate(token);
        String email = jwt.getClaim("email").asString();
        String username = jwt.getClaim("name").asString();

        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user = optionalUser.orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .username(username)
                    .password("") // OAuth 로그인이라 비밀번호는 빈 값
                    .role(UserRole.GUEST)
                    .city("")
                    .town("")
                    .address("")
                    .build();
            return userRepository.save(newUser);
        });

        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getId(),
                email,
                username,
                "",
                user.getCity(),
                user.getTown(),
                user.getRole(),
                user.getNx(),
                user.getNy()
        );

        // 인증 토큰 반환
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
