package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Сервис для управления задачами.
 *
 */
@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private Map<Integer, Task> taskCache;
    private final TaskRepository taskRepository;

    @Value("${app.name:To-Do List Manager}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        logger.info("TaskService создан с инжекцией через конструктор");
    }

    @PostConstruct
    public void init() {
        logger.info("=== @PostConstruct: НАЧАЛО инициализации кэша задач ===");
        logger.info("Приложение: {} версия {}", appName, appVersion);
        logger.info("Время инициализации: {}", LocalDateTime.now());

        taskCache = new ConcurrentHashMap<>();

        List<Task> allTasks = taskRepository.getAll();
        logger.info("Всего задач в репозитории: {}", allTasks.size());

        int loadedCount = 0;

        for (Task task : allTasks) {
            if (loadedCount < 5) {
                taskCache.put(task.getId(), task);
                logger.debug("Задача загружена в кэш: ID={}, название='{}'",
                    task.getId(), task.getTitle());
                loadedCount++;
            } else {
                break;
            }
        }

        logger.info("=== @PostConstruct: ЗАВЕРШЕНИЕ инициализации. Загружено {} задач в кэш ===",
            taskCache.size());
    }

    public Task readTask(int id) {
        logger.debug("Получение задачи по ID: {}", id);
        return taskRepository.readTask(id);
    }

    public Task createTask(String title, String description, boolean completed) {
        logger.debug("Создание новой задачи: {}", title);
        return taskRepository.createTask(title, description, completed);
    }

    public Task updateTask(int id, String title, String description, boolean completed) {
        if (taskRepository.readTask(id) != null) {
            taskRepository.updateTask(id, title, description, completed);
            return taskRepository.readTask(id);
        }
        return null;
    }

    public boolean deleteTask(int id) {
        if (taskRepository.readTask(id) != null) {
            taskRepository.deleteTask(id);
            return true;
        }
        return false;
    }

    public List<Task> getAll() {
        return taskRepository.getAll();
    }

    /**
     * Очистка ресурсов перед уничтожением бина.
     * Логирует статистику использования перед уничтожением.
     */
    @PreDestroy
    public void destroy() {
        logger.info("=== @PreDestroy: НАЧАЛО очистки ресурсов ===");
        logger.info("Время завершения: {}", LocalDateTime.now());
        logger.info("Приложение: {} версия {} завершает работу", appName, appVersion);

        logger.info("========== СТАТИСТИКА ПЕРЕД ЗАВЕРШЕНИЕМ ==========");
        logger.info("Количество задач в кэше: {}", taskCache.size());

        List<Task> allTasks = taskRepository.getAll();
        logger.info("Всего задач в репозитории: {}", allTasks.size());

        int completedCount = 0;
        for (Task task : allTasks) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }
        int activeCount = allTasks.size() - completedCount;

        logger.info("Выполненных задач: {}", completedCount);
        logger.info("Активных задач: {}", activeCount);

        // Логируем содержимое кэша
        if (!taskCache.isEmpty()) {
            logger.info("Задачи в кэше:");
            for (Map.Entry<Integer, Task> entry : taskCache.entrySet()) {
                Task task = entry.getValue();
                logger.info("  - ID: {}, Название: {}, Выполнена: {}",
                    task.getId(), task.getTitle(), task.isCompleted());
            }
        }

        logger.info("==================================================");

        taskCache.clear();
        logger.info("=== @PreDestroy: ЗАВЕРШЕНИЕ очистки. Кэш очищен ===");
    }
}
