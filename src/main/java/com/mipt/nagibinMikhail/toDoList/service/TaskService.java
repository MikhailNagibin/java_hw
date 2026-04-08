package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.exception.BulkUpdateException;
import com.mipt.nagibinMikhail.toDoList.mapper.TaskMapper;
import com.mipt.nagibinMikhail.toDoList.model.Priority;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Value("${app.name:To-Do List Manager}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @PostConstruct
    public void init() {
        log.info("=== @PostConstruct: НАЧАЛО инициализации TaskService ===");
        log.info("Приложение: {} версия {}", appName, appVersion);
        log.info("Время инициализации: {}", LocalDateTime.now());
        log.info("Всего задач в БД: {}", taskRepository.count());
        log.info("=== @PostConstruct: ЗАВЕРШЕНИЕ инициализации ===");
    }

    @PreDestroy
    public void destroy() {
        log.info("=== @PreDestroy: НАЧАЛО очистки ресурсов ===");
        log.info("Время завершения: {}", LocalDateTime.now());
        log.info("Приложение: {} версия {} завершает работу", appName, appVersion);

        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByCompleted(true);
        long activeTasks = totalTasks - completedTasks;

        log.info("========== СТАТИСТИКА ПЕРЕД ЗАВЕРШЕНИЕМ ==========");
        log.info("Всего задач в БД: {}", totalTasks);
        log.info("Выполненных задач: {}", completedTasks);
        log.info("Активных задач: {}", activeTasks);
        log.info("==================================================");
        log.info("=== @PreDestroy: ЗАВЕРШЕНИЕ очистки ===");
    }

    @Transactional(readOnly = true)
    public TaskModel readTask(int id) {
        log.debug("Получение задачи по ID: {}", id);
        return taskRepository.findById(id)
            .map(taskMapper::toModel)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public TaskModel createTask(String title, String description, boolean completed) {
        log.debug("Создание новой задачи: {}", title);
        Task task = Task.builder()
            .title(title)
            .description(description)
            .completed(completed)
            .build();
        Task saved = taskRepository.save(task);
        return taskMapper.toModel(saved);
    }

    @Transactional
    public TaskModel createTask(TaskModel taskModel) {
        log.debug("Создание новой задачи из модели: {}", taskModel.getTitle());
        Task task = taskMapper.toEntity(taskModel);
        Task saved = taskRepository.save(task);
        return taskMapper.toModel(saved);
    }

    @Transactional
    public TaskModel updateTask(int id, String title, String description, boolean completed) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);

        Task updated = taskRepository.save(task);
        return taskMapper.toModel(updated);
    }

    @Transactional
    public TaskModel updateTask(int id, TaskModel taskModel) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        taskMapper.updateEntity(task, taskModel);
        Task updated = taskRepository.save(task);
        return taskMapper.toModel(updated);
    }

    @Transactional
    public boolean deleteTask(int id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<TaskModel> getAll() {
        return taskRepository.findAll().stream()
            .map(taskMapper::toModel)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskModel> getTasksWithAttachments() {
        // Решение проблемы N+1 через JOIN FETCH
        return taskRepository.findAllWithAttachmentsJoinFetch().stream()
            .map(taskMapper::toModel)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskModel> getTasksByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed).stream()
            .map(taskMapper::toModel)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskModel> getTasksByCompletedAndPriority(boolean completed, Priority priority) {
        return taskRepository.findByCompletedAndPriority(completed, priority).stream()
            .map(taskMapper::toModel)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskModel> getTasksDueInNextWeek() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return taskRepository.findTasksDueInNextWeek(today, nextWeek).stream()
            .map(taskMapper::toModel)
            .collect(Collectors.toList());
    }

    /**
     * Массовое обновление задач как выполненных.
     * Демонстрация транзакционности:
     * - если в списке есть несуществующий ID, транзакция откатывается
     * - используется явно заданная конфигурация @Transactional
     *
     * @param ids список ID задач для обновления
     * @throws BulkUpdateException если хотя бы один ID не существует
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = {BulkUpdateException.class, RuntimeException.class},
        noRollbackFor = {}
    )
    public void bulkCompleteTasks(List<Integer> ids) {
        log.info("Начало массового обновления задач. IDs: {}", ids);

        if (ids == null || ids.isEmpty()) {
            log.warn("Пустой список IDs для массового обновления");
            return;
        }

        // Проверяем существование всех ID
        long existingCount = taskRepository.countByIdIn(ids);
        if (existingCount != ids.size()) {
            log.error("Не все задачи существуют. Ожидалось: {}, найдено: {}", ids.size(), existingCount);
            throw new BulkUpdateException(
                String.format("Not all tasks exist. Expected: %d, found: %d", ids.size(), existingCount)
            );
        }

        // Выполняем массовое обновление
        int updatedCount = taskRepository.markCompletedByIds(ids);
        log.info("Успешно обновлено {} задач", updatedCount);

        // Проверяем, что обновлены все
        if (updatedCount != ids.size()) {
            log.warn("Обновлено не всех задач. Ожидалось: {}, обновлено: {}", ids.size(), updatedCount);
            throw new BulkUpdateException(
                String.format("Update count mismatch. Expected: %d, updated: %d", ids.size(), updatedCount)
            );
        }
    }
}
