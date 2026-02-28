package com.mipt.nagibinMikhail.toDoList.Service;


import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Инжектирует оба репозитория для сравнения их работы и получения статистики.
 *
 */
@Service
public class TaskStatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(TaskStatisticsService.class);

    private final TaskRepository primaryRepository;
    private final TaskRepository stubRepository;

    @Autowired
    public TaskStatisticsService(
        TaskRepository primaryRepository,
        @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
        this.primaryRepository = primaryRepository;
        this.stubRepository = stubRepository;
        logger.info("TaskStatisticsService создан с инжекцией двух репозиториев");
    }

    /**
     * Сравнивает содержимое основного и заглушечного репозиториев.
     * Выводит статистику в лог.
     */
    public void compareRepositories() {
        List<Task> primaryTasks = primaryRepository.getAll();
        List<Task> stubTasks = stubRepository.getAll();

        logger.info("Основной репозиторий: {} задач", primaryTasks.size());
        logger.info("Stub репозиторий: {} задач", stubTasks.size());

        if (!primaryTasks.isEmpty()) {
            logger.info("Первая задача из основного репозитория: {}", primaryTasks.getFirst().getTitle());
        }

        if (!stubTasks.isEmpty()) {
            logger.info("Первая задача из stub репозитория: {}", stubTasks.getFirst().getTitle());
        }
    }

    /**
     * Получает статистику по задачам из указанного репозитория.
     * @param usePrimary true для основного репозитория, false для stub
     * @return строка со статистикой
     */
    public String getRepositoryStats(boolean usePrimary) {
        TaskRepository repository = usePrimary ? primaryRepository : stubRepository;
        List<Task> tasks = repository.getAll();

        long completedCount = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }

        long activeCount = tasks.size() - completedCount;

        String repoName = usePrimary ? "Основной" : "Stub";

        return "Репозиторий: " + repoName + "\n" +
            "Всего задач: " + tasks.size() + "\n" +
            "Выполнено: " + completedCount + "\n" +
            "Активно: " + activeCount;
    }
}
