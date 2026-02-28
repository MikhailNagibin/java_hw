package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.service.TaskService;
import com.mipt.nagibinMikhail.toDoList.service.TaskStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления задачами.
 * Предоставляет полный набор CRUD операций для работы с задачами.
 *
 * @author Student Name
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsService statisticsService;

    @Autowired
    public TaskController(TaskService taskService,
                          TaskStatisticsService statisticsService) {
        this.taskService = taskService;
        this.statisticsService = statisticsService;
    }

    /**
     * GET /api/tasks - получить все задачи
     *
     * @return список всех задач
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAll();
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/tasks/{id} - получить задачу по ID
     *
     * @param id идентификатор задачи
     * @return задача или 404 если не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer id) {
        Task task = taskService.readTask(id);

        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/tasks - создать новую задачу
     * @param task объект задачи из тела запроса
     * @return созданная задача
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Task createdTask = taskService.createTask(
            task.getTitle(),
            task.getDescription(),
            task.isCompleted()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * PUT /api/tasks/{id} - полностью обновить задачу
     *
     * @param id   идентификатор задачи
     * @param task обновленные данные задачи
     * @return обновленная задача или 404 если не найдена
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody Task task) {

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Task updatedTask = taskService.updateTask(
            id,
            task.getTitle(),
            task.getDescription(),
            task.isCompleted()
        );

        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /api/tasks/{id} - частично обновить задачу
     * @param id идентификатор задачи
     * @param updates частичные обновления задачи
     * @return обновленная задача или 404 если не найдена
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Task> partialUpdateTask(@PathVariable int id, @RequestBody Task updates) {

        Task existingTask = taskService.readTask(id);

        if (existingTask == null) {
            return ResponseEntity.notFound().build();
        }

        String title = updates.getTitle() != null ? updates.getTitle() : existingTask.getTitle();
        String description = updates.getDescription() != null ? updates.getDescription() : existingTask.getDescription();
        boolean completed = updates.isCompleted(); // если не передано, останется false - но это особенность boolean


        Task updatedTask = taskService.updateTask(id, title, description, completed);

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * DELETE /api/tasks/{id} - удалить задачу
     *
     * @param id идентификатор задачи
     * @return 204 если удалено, 404 если не найдена
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}