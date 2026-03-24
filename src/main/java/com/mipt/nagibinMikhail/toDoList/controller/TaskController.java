package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.dto.OnCreate;
import com.mipt.nagibinMikhail.toDoList.dto.OnUpdate;
import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskResponseDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskUpdateDto;
import com.mipt.nagibinMikhail.toDoList.exception.TaskNotFoundException;
import com.mipt.nagibinMikhail.toDoList.mapper.TaskMapper;
import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.service.TaskService;
import com.mipt.nagibinMikhail.toDoList.service.TaskStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления задачами.
 * Предоставляет полный набор CRUD операций для работы с задачами.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "Управление задачами")
public class TaskController {

    @Value("${api.version:2.0.0}")
    private String apiVersion;

    private final TaskService taskService;
    private final TaskStatisticsService statisticsService;
    private final TaskMapper taskMapper;

    /**
     * GET /api/tasks - получить все задачи
     *
     * @return список всех задач
     */
    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<Task> tasks = taskService.getAll();
        List<TaskResponseDto> response = tasks.stream()
            .map(taskMapper::toResponseDto)
            .toList();

        return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .header("X-Total-Count", String.valueOf(tasks.size()))
            .body(response);
    }

    /**
     * GET /api/tasks/{id} - получить задачу по ID
     *
     * @param id идентификатор задачи
     * @return задача или 404 если не найдена
     */
    @Operation(summary = "Получить задачу по ID", description = "Возвращает задачу по её идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно найдена",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable Integer id) {

        Task task = taskService.readTask(id);

        if (task != null) {
            return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(task));
        } else {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
    }

    /**
     * POST /api/tasks - создать новую задачу
     * @param createDto объект задачи из тела запроса
     * @return созданная задача
     */
    @Operation(summary = "Создать новую задачу", description = "Создаёт новую задачу")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Задача успешно создана",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
        @Validated(OnCreate.class) @RequestBody TaskCreateDto createDto) {

        Task task = taskMapper.toEntity(createDto);
        Task createdTask = taskService.createTask(
            task.getTitle(),
            task.getDescription(),
            false // новые задачи всегда невыполненные
        );

        // Дополняем созданную задачу полями из DTO, которых нет в сервисе
        createdTask.setDueDate(task.getDueDate());
        createdTask.setPriority(task.getPriority());
        createdTask.setTags(task.getTags());

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("X-API-Version", apiVersion)
            .body(taskMapper.toResponseDto(createdTask));
    }

    /**
     * PUT /api/tasks/{id} - полностью обновить задачу
     *
     * @param id   идентификатор задачи
     * @param updateDto обновленные данные задачи
     * @return обновленная задача или 404 если не найдена
     */
    @Operation(summary = "Полностью обновить задачу", description = "Обновляет все поля задачи")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable int id,
        @Validated(OnUpdate.class) @RequestBody TaskUpdateDto updateDto) {

        Task existingTask = taskService.readTask(id);
        if (existingTask == null) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }

        // Обновляем поля из DTO
        if (updateDto.getTitle() != null) {
            existingTask.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            existingTask.setDescription(updateDto.getDescription());
        }
        if (updateDto.getCompleted() != null) {
            existingTask.setCompleted(updateDto.getCompleted());
        }
        if (updateDto.getDueDate() != null) {
            existingTask.setDueDate(updateDto.getDueDate());
        }
        if (updateDto.getPriority() != null) {
            existingTask.setPriority(updateDto.getPriority());
        }
        if (updateDto.getTags() != null) {
            existingTask.setTags(updateDto.getTags());
        }

        Task updatedTask = taskService.updateTask(
            id,
            existingTask.getTitle(),
            existingTask.getDescription(),
            existingTask.isCompleted()
        );

        // Сервис не обновляет дополнительные поля, поэтому используем existingTask
        return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .body(taskMapper.toResponseDto(existingTask));
    }

    /**
     * PATCH /api/tasks/{id} - частично обновить задачу
     * @param id идентификатор задачи
     * @param updateDto частичные обновления задачи
     * @return обновленная задача или 404 если не найдена
     */
    @Operation(summary = "Частично обновить задачу", description = "Обновляет только переданные поля задачи")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDto> partialUpdateTask(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable int id,
        @Validated(OnUpdate.class) @RequestBody TaskUpdateDto updateDto) {

        Task existingTask = taskService.readTask(id);
        if (existingTask == null) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }

        // Применяем маппер для частичного обновления
        taskMapper.updateEntity(updateDto, existingTask);

        Task updatedTask = taskService.updateTask(
            id,
            existingTask.getTitle(),
            existingTask.getDescription(),
            existingTask.isCompleted()
        );

        return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .body(taskMapper.toResponseDto(existingTask));
    }

    /**
     * DELETE /api/tasks/{id} - удалить задачу
     *
     * @param id идентификатор задачи
     * @return 204 если удалено, 404 если не найдена
     */
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable int id) {

        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent()
                .header("X-API-Version", apiVersion)
                .build();
        } else {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
    }

    /**
     * GET /api/tasks/statistics/compare - сравнить репозитории
     * @return статистика сравнения
     */
    @Operation(summary = "Сравнить репозитории", description = "Сравнивает основной и stub репозитории")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статистика успешно получена")
    })
    @GetMapping("/statistics/compare")
    public ResponseEntity<String> compareRepositories() {
        statisticsService.compareRepositories();
        return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .body("Repository comparison completed. Check logs for details.");
    }
}
