package com.sounganization.botanify.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.dto.res.ExceptionResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.handler.JwtAuthorizationHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtAuthorizationHandler jwtAuthorizationHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip filter for auth endpoints
        if (request.getServletPath().startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtAuthorizationHandler.handleAuthorization(request);
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            response.setStatus(e.getStatus().getStatus().value());
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(
                    new ExceptionResDto(
                            e.getStatus().getStatus().value(),
                            e.getStatus().getMessage()
                    )
            ));
        }
    }
}