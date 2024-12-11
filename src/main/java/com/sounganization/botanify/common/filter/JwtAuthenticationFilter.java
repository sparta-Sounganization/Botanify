package com.sounganization.botanify.common.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sounganization.botanify.common.dto.res.ExceptionResDto;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> authRequest = mapper.readValue(request.getInputStream(), new TypeReference<>() {});
            String email = authRequest.get("email");
            String password = authRequest.get("password");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException("잘못된 인증 요청입니다.", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        String token = jwtUtil.generateToken(authResult);

        Cookie jwtCookie = new Cookie("Authorization", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");

        response.addCookie(jwtCookie);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\": 200, \"message\": \"로그인이 성공되었습니다.\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ExceptionStatus status = ExceptionStatus.INVALID_CREDENTIALS;
        String errorResponse = new ObjectMapper().writeValueAsString(
                new ExceptionResDto(status.getStatus().value(), status.getMessage())
        );

        response.getWriter().write(errorResponse);
    }
}
