package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskStatisticsServiceTest {

    @Autowired
    private TaskStatisticsService statisticsService;

    @Autowired
    private TaskRepository primaryRepository;

    @Autowired
    private TaskRepository stubRepository;

    @Test
    void compareRepositories_ShouldExecuteWithoutErrors() {
        // given - используем реальные репозитории
        // when
        statisticsService.compareRepositories();

        // then - просто проверяем, что метод выполнился без ошибок
        assertThat(primaryRepository.getAll()).isNotNull();
        assertThat(stubRepository.getAll()).isNotNull();
    }

    @Test
    void getRepositoryStats_ForPrimary_ShouldReturnStats() {
        // given
        primaryRepository.createTask("Test Task 1", "Description 1", false);
        primaryRepository.createTask("Test Task 2", "Description 2", true);

        // when
        String stats = statisticsService.getRepositoryStats(true);

        // then
        assertThat(stats).contains("Репозиторий: Основной");
        assertThat(stats).contains("Всего задач: 2");
        assertThat(stats).contains("Выполнено: 1");
        assertThat(stats).contains("Активно: 1");
    }

    @Test
    void getRepositoryStats_ForStub_ShouldReturnStats() {
        // given - stub репозиторий уже имеет предустановленные данные
        // when
        String stats = statisticsService.getRepositoryStats(false);

        // then
        assertThat(stats).contains("Репозиторий: Stub");
        assertThat(stats).contains("Всего задач: 1");
        assertThat(stats).contains("Выполнено: 0");
        assertThat(stats).contains("Активно: 1");
    }

    @Test
    void getRepositoryStats_EmptyPrimary_ShouldReturnZeroCounts() {
        // given - очищаем репозиторий
        primaryRepository.getAll().forEach(task -> primaryRepository.deleteTask(task.getId()));

        // when
        String stats = statisticsService.getRepositoryStats(true);

        // then
        assertThat(stats).contains("Всего задач: 0");
        assertThat(stats).contains("Выполнено: 0");
        assertThat(stats).contains("Активно: 0");
    }
}
