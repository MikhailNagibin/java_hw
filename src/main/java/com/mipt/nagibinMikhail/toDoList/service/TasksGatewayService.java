package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.client.ExternalTasksClient;
import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskDto;
import com.mipt.nagibinMikhail.toDoList.exception.ExternalApiException;
import com.mipt.nagibinMikhail.toDoList.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackCreateTask")
    @RateLimiter(name = "externalApi")
    @Retry(name = "externalApi")
    public TaskDto createTask(TaskCreateDto taskDto) {
        log.info("Creating task via gateway: {}", taskDto.getTitle());
        return externalTasksClient.createTask(taskDto);
    }

    private TaskDto fallbackCreateTask(TaskCreateDto taskDto, Throwable t) {
        log.warn("Circuit breaker fallback for createTask: {}", t.getMessage());
        return TaskDto.builder()
            .id(-1L)
            .title("FALLBACK: " + taskDto.getTitle())
            .description("Task creation failed due to external service unavailability")
            .completed(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackGetTask")
    @RateLimiter(name = "externalApi")
    @Retry(name = "externalApi")
    public TaskDto getTask(Long id) {
        log.info("Getting task via gateway: {}", id);
        return externalTasksClient.getTask(id);
    }

    private TaskDto fallbackGetTask(Long id, Throwable t) {
        log.warn("Circuit breaker fallback for getTask: {} - {}", id, t.getMessage());

        if (t instanceof TaskNotFoundException) {
            throw (TaskNotFoundException) t;
        }

        return TaskDto.builder()
            .id(id)
            .title("FALLBACK TASK")
            .description("Task retrieval failed due to external service unavailability")
            .completed(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackGetTasks")
    @RateLimiter(name = "externalApi")
    @Retry(name = "externalApi")
    public List<TaskDto> getTasks(Boolean completed, int limit) {
        log.info("Getting tasks via gateway: completed={}, limit={}", completed, limit);
        return externalTasksClient.getTasks(completed, limit);
    }

    private List<TaskDto> fallbackGetTasks(Boolean completed, int limit, Throwable t) {
        log.warn("Circuit breaker fallback for getTasks: {}", t.getMessage());
        return List.of(
            TaskDto.builder()
                .id(-1L)
                .title("FALLBACK TASK LIST")
                .description("Tasks retrieval failed due to external service unavailability")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackDeleteTask")
    @RateLimiter(name = "externalApi")
    @Retry(name = "externalApi")
    public void deleteTask(Long id) {
        log.info("Deleting task via gateway: {}", id);
        externalTasksClient.deleteTask(id);
    }

    private void fallbackDeleteTask(Long id, Throwable t) {
        log.warn("Circuit breaker fallback for deleteTask: {} - {}", id, t.getMessage());

        if (t instanceof TaskNotFoundException) {
            throw (TaskNotFoundException) t;
        }

        throw new ExternalApiException(503, "External service unavailable for delete operation");
    }
}