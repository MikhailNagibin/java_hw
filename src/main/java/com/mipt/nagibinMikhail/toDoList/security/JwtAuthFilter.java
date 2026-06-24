package com.mipt.nagibinMikhail.toDoList.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        // Пропускаем публичные эндпоинты
        if (path.startsWith("/api/v1/auth/login") ||
            path.startsWith("/actuator") ||
            path.startsWith("/external") ||
            path.startsWith("/h2-console") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token != null) {
            log.debug("JWT token extracted: {}", jwtUtils.maskToken(token));
            if (jwtUtils.validateToken(token)) {
                Authentication auth = jwtUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Authentication set for user: {}", auth.getName());
            } else {
                log.warn("Invalid JWT token: {}", jwtUtils.maskToken(token));
            }
        } else {
            log.debug("No JWT token found in Authorization header");
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
