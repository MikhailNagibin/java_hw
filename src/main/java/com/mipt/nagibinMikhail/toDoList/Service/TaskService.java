package com.mipt.nagibinMikhail.toDoList.Service;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Сервис для управления задачами.
 *
 */
@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        logger.info("TaskService создан с инжекцией через конструктор");
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

    public void deleteTask(int id) {
        if (taskRepository.readTask(id) != null) {
            taskRepository.deleteTask(id);
        }
    }
}
