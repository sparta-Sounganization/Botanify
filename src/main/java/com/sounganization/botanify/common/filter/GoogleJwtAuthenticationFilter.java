package com.sounganization.botanify.common.filter;

import com.sounganization.botanify.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.PrintWriter;

public class GoogleJwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtUtil jwtUtil;

    public GoogleJwtAuthenticationFilter(String defaultFilterProcessesUrl,
                                         AuthenticationManager authenticationManager,
                                         JwtUtil jwtUtil) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        // Authorization 헤더 확인 및 Null 체크
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("인증 헤더가 없거나 잘못되었습니다");
        }

        String token = authorizationHeader.replace("Bearer ", "");
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(null, token));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        // JWT 생성
        String jwt = jwtUtil.generateToken(authResult);

        // JWT를 쿠키에 추가
        jwtUtil.addJwtToCookie(jwt, response);

        // JSON 응답 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = "{\n" +
                "    \"status\": 200,\n" +
                "    \"message\": \"Google 로그인이 성공되었습니다.\"\n" +
                "}";

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = "{\n" +
                "    \"status\": 401,\n" +
                "    \"message\": \"Google 로그인 실패: " + failed.getMessage() + "\"\n" +
                "}";

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
        }
}
