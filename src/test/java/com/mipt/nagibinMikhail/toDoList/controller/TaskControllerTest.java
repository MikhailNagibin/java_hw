package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты для TaskController.
 * Тестирует все CRUD endpoint'ы с позитивными и негативными сценариями.
 *
 * @author Student Name
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private String baseUrl;
    private Task testTask;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/tasks";

        for (Task task : taskRepository.getAll()) {
            taskRepository.deleteTask(task.getId());
        }

        testTask = taskRepository.createTask("Тестовая задача", "Описание тестовой задачи", false);
    }


    /**
     * Позитивный тест: получение всех задач
     */
    @Test
    void testGetAllTasks_Success() {
        taskRepository.createTask("Задача 1", "Описание 1", false);
        taskRepository.createTask("Задача 2", "Описание 2", true);

        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(3); // testTask + 2 новые
    }

    /**
     * Негативный тест: получение всех задач из пустого репозитория
     */
    @Test
    void testGetAllTasks_EmptyRepository() {
        for (Task task : taskRepository.getAll()) {
            taskRepository.deleteTask(task.getId());
        }

        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(0);
    }


    /**
     * Позитивный тест: получение задачи по существующему ID
     */
    @Test
    void testGetTaskById_Success() {
        int taskId = testTask.getId();

        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/" + taskId, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(taskId);
        assertThat(response.getBody().getTitle()).isEqualTo("Тестовая задача");
    }

    /**
     * Негативный тест: получение задачи по несуществующему ID
     */
    @Test
    void testGetTaskById_NotFound() {
        int nonExistentId = 99999;

        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/" + nonExistentId, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    /**
     * Негативный тест: получение задачи по некорректному ID (не число)
     */
    @Test
    void testGetTaskById_InvalidIdFormat() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/invalid-id", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    /**
     * Позитивный тест: создание новой задачи
     */
    @Test
    void testCreateTask_Success() {
        Task newTask = taskRepository.createTask("Новая задача", "Описание новой задачи", false);

        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, newTask, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Новая задача");
        assertThat(response.getBody().getDescription()).isEqualTo("Описание новой задачи");
        assertThat(response.getBody().isCompleted()).isFalse();

        Task createdTask = taskRepository.readTask(response.getBody().getId());
        assertThat(createdTask).isNotNull();
    }

    /**
     * Негативный тест: создание задачи с пустым заголовком
     */
    @Test
    void testCreateTask_EmptyTitle() {
        Task invalidTask = new Task();
        invalidTask.setTitle("");
        invalidTask.setDescription("Описание");
        invalidTask.setCompleted(false);

        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, invalidTask, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Негативный тест: создание задачи с null заголовком
     */
    @Test
    void testCreateTask_NullTitle() {
        Task invalidTask = new Task();
        invalidTask.setTitle(null);
        invalidTask.setDescription("Описание");
        invalidTask.setCompleted(false);

        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, invalidTask, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Негативный тест: создание задачи с пустым телом запроса
     */
    @Test
    void testCreateTask_EmptyBody() {
        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, null, Task.class);

        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }


    /**
     * Позитивный тест: полное обновление существующей задачи
     */
    @Test
    void testUpdateTask_Success() {
        int taskId = testTask.getId();
        Task updatedTask = new Task();
        updatedTask.setTitle("Обновленная задача");
        updatedTask.setDescription("Новое описание");
        updatedTask.setCompleted(true);

        HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask);

        ResponseEntity<Task> response = restTemplate.exchange(
            baseUrl + "/" + taskId,
            HttpMethod.PUT,
            requestEntity,
            Task.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(taskId);
        assertThat(response.getBody().getTitle()).isEqualTo("Обновленная задача");
        assertThat(response.getBody().getDescription()).isEqualTo("Новое описание");
        assertThat(response.getBody().isCompleted()).isTrue();

        Task taskFromDb = taskRepository.readTask(taskId);
        assertThat(taskFromDb.getTitle()).isEqualTo("Обновленная задача");
    }

    /**
     * Негативный тест: обновление несуществующей задачи
     */
    @Test
    void testUpdateTask_NotFound() {
        int nonExistentId = 99999;
        Task updatedTask = new Task();
        updatedTask.setTitle("Обновленная задача");
        updatedTask.setDescription("Новое описание");
        updatedTask.setCompleted(true);

        HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask);

        ResponseEntity<Task> response = restTemplate.exchange(
            baseUrl + "/" + nonExistentId,
            HttpMethod.PUT,
            requestEntity,
            Task.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Негативный тест: обновление задачи с пустым заголовком
     */
    @Test
    void testUpdateTask_EmptyTitle() {
        int taskId = testTask.getId();
        Task invalidTask = new Task();
        invalidTask.setTitle("");
        invalidTask.setDescription("Новое описание");
        invalidTask.setCompleted(true);

        HttpEntity<Task> requestEntity = new HttpEntity<>(invalidTask);

        ResponseEntity<Task> response = restTemplate.exchange(
            baseUrl + "/" + taskId,
            HttpMethod.PUT,
            requestEntity,
            Task.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    /**
     * Позитивный тест: частичное обновление задачи (только статус)
     */
    @Test
    void testPartialUpdateTask_StatusOnly() {
        int taskId = testTask.getId();
        Task partialUpdate = new Task();
        partialUpdate.setCompleted(true);

        HttpEntity<Task> requestEntity = new HttpEntity<>(partialUpdate);

        ResponseEntity<Task> response = restTemplate.exchange(
            baseUrl + "/" + taskId,
            HttpMethod.PATCH,
            requestEntity,
            Task.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(taskId);
        assertThat(response.getBody().getTitle()).isEqualTo("Тестовая задача"); // Не изменилось
        assertThat(response.getBody().getDescription()).isEqualTo("Описание тестовой задачи"); // Не изменилось
        assertThat(response.getBody().isCompleted()).isTrue(); // Изменилось
    }

    /**
     * Позитивный тест: частичное обновление задачи (только заголовок)
     */
    @Test
    void testPartialUpdateTask_TitleOnly() {
        int taskId = testTask.getId();
        Task partialUpdate = new Task();
        partialUpdate.setTitle("Новый заголовок");

        HttpEntity<Task> requestEntity = new HttpEntity<>(partialUpdate);

        ResponseEntity<Task> response = restTemplate.exchange(
            baseUrl + "/" + taskId,
            HttpMethod.PATCH,
            requestEntity,
            Task.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Новый заголовок");
        assertThat(response.getBody().getDescription()).isEqualTo("Описание тестовой задачи"); // Не изменилось
        assertThat(response.getBody().isCompleted()).isFalse(); // Не изменилось
    }

    /**
     * Негативный тест: частичное обновление несуществующей задачи
     */
    @Test
    void testPartialUpdateTask_NotFound() {
        int nonExistentId = 99999;
        Task partialUpdate = new Task();
        partialUpdate.setCompleted(true);

        HttpEntity<Task> requestEntity = new HttpEntity<>(partialUpdate);

        ResponseEntity<Task> response = restTemplate.exchange(
            baseUrl + "/" + nonExistentId,
            HttpMethod.PATCH,
            requestEntity,
            Task.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    /**
     * Позитивный тест: удаление существующей задачи
     */
    @Test
    void testDeleteTask_Success() {
        int taskId = testTask.getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl + "/" + taskId,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Task deletedTask = taskRepository.readTask(taskId);
        assertThat(deletedTask).isNull();
    }

    /**
     * Негативный тест: удаление несуществующей задачи
     */
    @Test
    void testDeleteTask_NotFound() {
        int nonExistentId = 99999;

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl + "/" + nonExistentId,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Негативный тест: удаление задачи с некорректным ID
     */
    @Test
    void testDeleteTask_InvalidId() {
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/invalid-id",
            HttpMethod.DELETE,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}