package com.mipt.nagibinMikhail.toDoList.repository;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

    static {
        System.setProperty("docker.client.version", "1.41");
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2")
        .withDatabaseName("todolist")
        .withUsername("postgres")
        .withPassword("qwerty");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldFindTasksDueInNextWeek() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        Task task1 = Task.builder()
            .title("Due today")
            .description("")
            .completed(false)
            .dueDate(today)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Task task2 = Task.builder()
            .title("Due next week")
            .description("")
            .completed(false)
            .dueDate(today.plusDays(5))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Task task3 = Task.builder()
            .title("Due later")
            .description("")
            .completed(false)
            .dueDate(today.plusDays(10))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        List<Task> tasks = taskRepository.findTasksDueInNextWeek(today, nextWeek);

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Due today", "Due next week");
    }
}
