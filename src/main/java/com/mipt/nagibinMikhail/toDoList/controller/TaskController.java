package com.mipt.nagibinMikhail.toDoList.controller;


import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Простой REST контроллер для проверки работы CRUD операций.
 * Предоставляет базовые endpoints для управления задачами.
 *
 * @author Student Name
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
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
        return task != null ? ResponseEntity.ok(taskService.readTask(id)) : ResponseEntity.notFound().build();
    }

    /**
     * POST /api/tasks - создать новую задачу
     *
     * @param title  название
     * @param description  описание
     * @param complete  состояние выполненности
     * @return созданная задача
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody String title,
                                           @RequestBody String description, @RequestBody boolean complete) {
        Task createdTask = taskService.createTask(title, description, complete);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * PUT /api/tasks/{id} - обновить задачу
     *
     * @param id   идентификатор задачи
     * @param title обновленное название
     * @param description обновленное описание
     * @param complete обновленное состояния выполненности
     * @return обновленная задача или 404 если не найдена
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody String title,
                                           @RequestBody String description, @RequestBody boolean complete) {
        Task task = taskService.updateTask(id, title, description, complete);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
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