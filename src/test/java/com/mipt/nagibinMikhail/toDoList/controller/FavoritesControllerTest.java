package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.mapper.TaskMapper;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import com.mipt.nagibinMikhail.toDoList.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesController.class)
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    void addToFavorites_TaskExists_ShouldAddToSession() throws Exception {
        TaskModel task = new TaskModel();
        task.setId(1);
        when(taskService.readTask(1)).thenReturn(task);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/1").session(session))
            .andExpect(status().isOk());

        @SuppressWarnings("unchecked")
        Set<Integer> favorites = (Set<Integer>) session.getAttribute("favoriteTaskIds");
        assert favorites != null && favorites.contains(1);
    }

    @Test
    void addToFavorites_TaskNotFound_ShouldReturn404() throws Exception {
        when(taskService.readTask(99)).thenReturn(null);

        mockMvc.perform(post("/api/favorites/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getFavorites_EmptySession_ShouldReturnEmptyList() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/api/favorites").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
