package com.mipt.nagibinMikhail.toDoList.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskDto;
import com.mipt.nagibinMikhail.toDoList.security.JwtUtils;
import com.mipt.nagibinMikhail.toDoList.service.TasksGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    value = TaskController.class,
    excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        FlywayAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "jpa.auditing.enabled=false")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TasksGatewayService tasksGatewayService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_shouldReturn201AndTask() throws Exception {
        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("New Task");
        createDto.setDescription("Description");
        createDto.setCompleted(false);

        TaskDto responseDto = TaskDto.builder()
            .id(1L)
            .title("New Task")
            .description("Description")
            .completed(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(tasksGatewayService.createTask(any(TaskCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("New Task"))
            .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getTaskById_shouldReturn200AndTask() throws Exception {
        Long taskId = 1L;
        TaskDto responseDto = TaskDto.builder()
            .id(taskId)
            .title("Existing Task")
            .description("Desc")
            .completed(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(tasksGatewayService.getTask(taskId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.title").value("Existing Task"))
            .andExpect(jsonPath("$.completed").value(true));
    }
}
