package com.mipt.nagibinMikhail.toDoList.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreferencesController.class)
class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getViewPreference_WithCookie_ShouldReturnCookieValue() throws Exception {
        mockMvc.perform(get("/api/preferences/view")
                .cookie(new Cookie("viewPreference", "compact")))
            .andExpect(status().isOk())
            .andExpect(header().string("X-API-Version", "2.0.0"))
            .andExpect(content().string("Current view preference: compact"));
    }

    @Test
    void getViewPreference_WithoutCookie_ShouldReturnDefault() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
            .andExpect(status().isOk())
            .andExpect(content().string("Current view preference: detailed"));
    }

    @Test
    void setViewPreference_ValidMode_ShouldSetCookie() throws Exception {
        mockMvc.perform(post("/api/preferences/view")
                .param("mode", "compact"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-API-Version", "2.0.0"))
            .andExpect(cookie().value("viewPreference", "compact"))
            .andExpect(cookie().httpOnly("viewPreference", true))
            .andExpect(cookie().maxAge("viewPreference", 30 * 24 * 60 * 60))
            .andExpect(cookie().path("viewPreference", "/"))
            .andExpect(content().string("View preference set to: compact"));
    }

    @Test
    void setViewPreference_InvalidMode_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/preferences/view")
                .param("mode", "invalid"))
            .andExpect(status().isBadRequest())
            .andExpect(header().string("X-API-Version", "2.0.0"))
            .andExpect(content().string("Mode must be either 'compact' or 'detailed'"));
    }
}
