package com.mipt.nagibinMikhail.toDoList.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long expirationMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Utils initialized with secret length: {} bytes", secret.getBytes(StandardCharsets.UTF_8).length);
    }

    public String generateToken(String username, List<String> authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(username)
            .claim("authorities", authorities)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        String username = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);

        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        UserDetails userDetails = User.builder()
            .username(username)
            .password("")
            .authorities(grantedAuthorities)
            .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, grantedAuthorities);
    }

    public String maskToken(String token) {
        if (token == null || token.length() < 12) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}
