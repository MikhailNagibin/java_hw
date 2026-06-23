package com.mipt.nagibinMikhail.toDoList.logging;

import com.mipt.nagibinMikhail.toDoList.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
@Slf4j
public class AccessLogFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_MDC_KEY = "traceId";
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = path + (queryString != null ? "?" + queryString : "");

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = wrappedResponse.getStatus();
            String traceId = MDC.get(TRACE_ID_MDC_KEY);

            // Логируем access log
            log.info("HTTP {} {} -> status={} timeMs={} trace={}",
                method, fullPath, status, duration, traceId);

            // Логируем тело запроса/ответа для ошибок
            if (status >= 400) {
                logErrorDetails(wrappedRequest, wrappedResponse, status, traceId);
            }

            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logErrorDetails(ContentCachingRequestWrapper request,
                                 ContentCachingResponseWrapper response,
                                 int status,
                                 String traceId) {

        // Логируем тело запроса (маскируем секреты)
        byte[] requestBody = request.getContentAsByteArray();
        if (requestBody.length > 0) {
            String body = new String(requestBody, StandardCharsets.UTF_8);
            String maskedBody = maskSensitiveData(body);
            log.warn("Request body (masked): {} trace={}", maskedBody, traceId);
        }

        // Логируем тело ответа (ограничиваем размер)
        byte[] responseBody = response.getContentAsByteArray();
        if (responseBody.length > 0) {
            String body = new String(responseBody, StandardCharsets.UTF_8);
            if (body.length() > 1000) {
                body = body.substring(0, 1000) + "... [truncated]";
            }
            log.warn("Response body: {} trace={}", body, traceId);
        }
    }

    private String maskSensitiveData(String body) {
        // Маскируем пароли и токены
        return body
            .replaceAll("(?i)\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"****\"")
            .replaceAll("(?i)\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"***\"")
            .replaceAll("(?i)\"accessToken\"\\s*:\\s*\"[^\"]*\"", "\"accessToken\":\"***\"")
            .replaceAll("Bearer\\s+[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+",
                "Bearer " + jwtUtils.maskToken("placeholder"));
    }
}
