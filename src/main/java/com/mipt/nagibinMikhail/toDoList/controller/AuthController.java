package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.dto.AuthRequest;
import com.mipt.nagibinMikhail.toDoList.dto.AuthResponse;
import com.mipt.nagibinMikhail.toDoList.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        String token = authService.authenticate(
            request.getUsername(),
            request.getPassword()
        );

        log.info("User logged in successfully: {}", request.getUsername());
        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .username(request.getUsername())
            .build();
    }
}
