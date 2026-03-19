package com.mipt.nagibinMikhail.toDoList.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

    private static final String VIEW_PREFERENCE_COOKIE = "viewPreference";
    private static final String DEFAULT_VIEW = "detailed";

    @GetMapping("/view")
    public ResponseEntity<String> getViewPreference(@CookieValue(value = VIEW_PREFERENCE_COOKIE, defaultValue = DEFAULT_VIEW) String viewPreference) {
        return ResponseEntity.ok("Current view preference: " + viewPreference);
    }

    @PostMapping("/view")
    public ResponseEntity<String> setViewPreference(@RequestParam String mode, HttpServletResponse response) {
        if (!mode.equals("compact") && !mode.equals("detailed")) {
            return ResponseEntity.badRequest().body("Mode must be either 'compact' or 'detailed'");
        }

        Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE, mode);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
        return ResponseEntity.ok("View preference set to: " + mode);
    }
}
