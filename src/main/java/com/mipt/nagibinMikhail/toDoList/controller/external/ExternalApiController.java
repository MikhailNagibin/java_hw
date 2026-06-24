package com.mipt.nagibinMikhail.toDoList.controller.external;

import com.mipt.nagibinMikhail.toDoList.dto.TaskDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
@Slf4j
public class ExternalApiController {

    private final Map<Long, TaskDto> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        // Добавляем несколько тестовых задач
        TaskDto task1 = new TaskDto(idGenerator.getAndIncrement(), "Test Task 1", "Description 1", false, LocalDateTime.now(), LocalDateTime.now());
        TaskDto task2 = new TaskDto(idGenerator.getAndIncrement(), "Test Task 2", "Description 2", true, LocalDateTime.now(), LocalDateTime.now());
        tasks.put(task1.getId(), task1);
        tasks.put(task2.getId(), task2);
        log.info("Initialized external API with {} tasks", tasks.size());
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskCreateDto taskDto) {
        log.info("External API: Creating task: {}", taskDto.getTitle());

        TaskDto task = new TaskDto(
            idGenerator.getAndIncrement(),
            taskDto.getTitle(),
            taskDto.getDescription(),
            taskDto.getCompleted() != null ? taskDto.getCompleted() : false,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        tasks.put(task.getId(), task);

        return ResponseEntity
            .created(URI.create("/external/v1/tasks/" + task.getId()))
            .body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        log.info("External API: Getting task with id: {}", id);

        TaskDto task = tasks.get(id);
        if (task == null) {
            log.warn("External API: Task not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getTasks(
        @RequestParam(required = false) Boolean completed,
        @RequestParam(defaultValue = "10") int limit) {
        log.info("External API: Getting tasks with completed={}, limit={}", completed, limit);

        List<TaskDto> result = new ArrayList<>(tasks.values());

        if (completed != null) {
            result = result.stream()
                .filter(t -> t.getCompleted().equals(completed))
                .toList();
        }

        if (result.size() > limit) {
            result = result.subList(0, limit);
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("External API: Deleting task with id: {}", id);

        TaskDto removed = tasks.remove(id);
        if (removed == null) {
            log.warn("External API: Task not found for deletion: {}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstableEndpoint(
        @RequestParam String mode,
        @RequestParam(required = false) Integer delay) throws InterruptedException {

        log.info("External API: Unstable endpoint called with mode: {}", mode);

        switch (mode.toLowerCase()) {
            case "timeout":
                log.info("External API: Simulating timeout - sleeping for 10 seconds");
                Thread.sleep(10000); // 10 seconds timeout
                return ResponseEntity.ok("Should not reach here");

            case "500":
                log.warn("External API: Returning 500 Internal Server Error");
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Internal Server Error",
                        "message", "Simulated server error",
                        "timestamp", System.currentTimeMillis()
                    ));

            case "429":
                log.warn("External API: Returning 429 Too Many Requests");
                return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", "30")
                    .body(Map.of(
                        "error", "Too Many Requests",
                        "message", "Rate limit exceeded",
                        "retryAfter", 30
                    ));

            case "html":
                log.warn("External API: Returning HTML instead of JSON (502)");
                return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>502 Bad Gateway</h1><p>HTML error page</p></body></html>");

            default:
                log.warn("External API: Invalid mode: {}", mode);
                return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                        "error", "Bad Request",
                        "message", "Invalid mode. Supported: timeout, 500, 429, html"
                    ));
        }
    }
}
