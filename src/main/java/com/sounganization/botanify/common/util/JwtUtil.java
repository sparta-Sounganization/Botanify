package com.sounganization.botanify.common.util;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.user.enums.UserRole;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${spring.jwt.secret.key}")
    private String secretKey;

    @Value("${spring.jwt.secret.expiration}")
    private long expirationTime;

    private Key signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = getSigningKey();
    }

    // JWT 서명 키 생성
    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(String.valueOf(userDetails.getId()))
                .claim("username", userDetails.getUsername())
                .claim("role", userDetails.getRole().name())
                .claim("city", userDetails.getCity())
                .claim("town", userDetails.getTown())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey)
                .compact();
    }

    // 토큰에서 Claims 추출
    public Claims getClaimsFromToken(String token) {
        return parseToken(token);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        parseToken(token);
        return true;
    }

    // 토큰을 파싱하여 Claims 객체로 변환
    private Claims parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new CustomException(ExceptionStatus.TOKEN_NOT_PROVIDED);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ExceptionStatus.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw new CustomException(ExceptionStatus.INVALID_TOKEN);
        } catch (Exception e) {
            throw new CustomException(ExceptionStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);

        Long id = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);
        String password = ""; // 비밀번호는 토큰에 저장하지 않음
        String city = claims.get("city", String.class);
        String town = claims.get("town", String.class);
        String role = claims.get("role", String.class);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                id, username, password, city, town, UserRole.valueOf(role));

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
