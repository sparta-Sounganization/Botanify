package com.sounganization.botanify.common.filter;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("GET".equals(request.getMethod()) &&
                (
                        request.getServletPath().equals("/api/v1/posts") ||
                        request.getServletPath().matches("/api/v1/posts/\\d+") ||
                        request.getServletPath().matches("/api/v1/species/\\d+")
                )
        ) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            jwtAuthorizationHandler.handleAuthorization(request);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\": 403, \"message\": \"접근 거부된 페이지입니다.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
