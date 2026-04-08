package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.mapper.TaskMapper;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStatisticsService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Сравнивает содержимое основного и заглушечного репозиториев.
     * Выводит статистику в лог.
     */
    public void compareRepositories() {
        List<Task> tasks = taskRepository.findAll();

        log.info("========== СТАТИСТИКА ЗАДАЧ ==========");
        log.info("Всего задач в репозитории: {}", tasks.size());

        long completedCount = tasks.stream()
            .filter(Task::isCompleted)
            .count();
        long activeCount = tasks.size() - completedCount;

        log.info("Выполненных задач: {}", completedCount);
        log.info("Активных задач: {}", activeCount);

        // Статистика по приоритетам
        long highPriority = tasks.stream()
            .filter(t -> t.getPriority() != null && t.getPriority().name().equals("HIGH"))
            .count();
        long mediumPriority = tasks.stream()
            .filter(t -> t.getPriority() != null && t.getPriority().name().equals("MEDIUM"))
            .count();
        long lowPriority = tasks.stream()
            .filter(t -> t.getPriority() != null && t.getPriority().name().equals("LOW"))
            .count();

        log.info("Задач с приоритетом HIGH: {}", highPriority);
        log.info("Задач с приоритетом MEDIUM: {}", mediumPriority);
        log.info("Задач с приоритетом LOW: {}", lowPriority);

        if (!tasks.isEmpty()) {
            log.info("Первая задача: ID={}, название='{}'",
                tasks.getFirst().getId(), tasks.getFirst().getTitle());
        }

        log.info("=====================================");
    }

    /**
     * Получает статистику по задачам из указанного репозитория.
     * @return строка со статистикой
     */
    public String getRepositoryStats() {
        List<Task> tasks = taskRepository.findAll();

        long completedCount = tasks.stream()
            .filter(Task::isCompleted)
            .count();

        long activeCount = tasks.size() - completedCount;

        return "Репозиторий: Основной\n" +
            "Всего задач: " + tasks.size() + "\n" +
            "Выполнено: " + completedCount + "\n" +
            "Активно: " + activeCount;
    }
}