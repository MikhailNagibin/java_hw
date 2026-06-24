package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskDto;
import com.mipt.nagibinMikhail.toDoList.service.TasksGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST контроллер для управления задачами через внешний API Gateway.
 * Все методы защищены JWT аутентификацией и используют Resilience4j.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "Управление задачами через внешний API")
@Slf4j
public class TaskController {

    private final TasksGatewayService tasksGatewayService;

    /**
     * POST /api/v1/tasks - создать новую задачу
     * @param taskDto объект задачи из тела запроса
     * @return созданная задача с Location header
     */
    @Operation(summary = "Создать новую задачу", description = "Создаёт задачу через внешний API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "429", description = "Слишком много запросов"),
        @ApiResponse(responseCode = "503", description = "Внешний сервис недоступен")
    })
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto taskDto) {
        log.info("Creating task via gateway: {}", taskDto.getTitle());
        TaskDto created = tasksGatewayService.createTask(taskDto);
        return ResponseEntity
            .created(URI.create("/api/v1/tasks/" + created.getId()))
            .body(created);
    }

    /**
     * GET /api/v1/tasks/{id} - получить задачу по ID
     * @param id идентификатор задачи
     * @return задача или 404 если не найдена
     */
    @Operation(summary = "Получить задачу по ID", description = "Возвращает задачу из внешнего API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно найдена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "429", description = "Слишком много запросов"),
        @ApiResponse(responseCode = "503", description = "Внешний сервис недоступен")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable Long id) {
        log.info("Getting task via gateway: {}", id);
        TaskDto task = tasksGatewayService.getTask(id);
        return ResponseEntity.ok(task);
    }

    /**
     * GET /api/v1/tasks - получить список задач с фильтрацией
     * @param completed фильтр по статусу выполнения
     * @param limit максимальное количество задач
     * @return список задач
     */
    @Operation(summary = "Получить список задач", description = "Возвращает список задач с фильтрацией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список задач успешно получен"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "429", description = "Слишком много запросов"),
        @ApiResponse(responseCode = "503", description = "Внешний сервис недоступен")
    })
    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
        @RequestParam(required = false) Boolean completed,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        log.info("Getting tasks via gateway: completed={}, limit={}", completed, limit);
        List<TaskDto> tasks = tasksGatewayService.getTasks(completed, limit);
        return ResponseEntity.ok(tasks);
    }

    /**
     * DELETE /api/v1/tasks/{id} - удалить задачу
     * @param id идентификатор задачи
     * @return 204 если удалено
     */
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу из внешнего API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "429", description = "Слишком много запросов"),
        @ApiResponse(responseCode = "503", description = "Внешний сервис недоступен")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable Long id) {
        log.info("Deleting task via gateway: {}", id);
        tasksGatewayService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/profile - получить профиль пользователя
     * Требует ROLE_USER
     */
    @Operation(summary = "Получить профиль", description = "Возвращает профиль пользователя (требует ROLE_USER)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль успешно получен"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getProfile() {
        return ResponseEntity.ok("User profile accessed successfully");
    }

    /**
     * GET /api/v1/docs - получить документацию
     * Требует authority READ_PRIVILEGE
     */
    @Operation(summary = "Получить документацию", description = "Возвращает документацию (требует READ_PRIVILEGE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Документация успешно получена"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/docs")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE')")
    public ResponseEntity<String> getDocs() {
        return ResponseEntity.ok("Documentation accessed successfully");
    }
}