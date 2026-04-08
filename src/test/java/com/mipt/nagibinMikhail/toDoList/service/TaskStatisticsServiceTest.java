//package com.mipt.nagibinMikhail.toDoList.service;
//
//import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//class TaskStatisticsServiceTest {
//
//    @Autowired
//    private TaskStatisticsService statisticsService;
//
//    @Autowired
//    private TaskRepository primaryRepository;
//
//    @Autowired
//    private TaskRepository stubRepository;
//
//    @Test
//    void compareRepositories_ShouldExecuteWithoutErrors() {
//        statisticsService.compareRepositories();
//
//        assertThat(primaryRepository.getAll()).isNotNull();
//        assertThat(stubRepository.getAll()).isNotNull();
//    }
//
//    @Test
//    void getRepositoryStats_ForPrimary_ShouldReturnStats() {
//        primaryRepository.createTask("Test Task 1", "Description 1", false);
//        primaryRepository.createTask("Test Task 2", "Description 2", true);
//
//        String stats = statisticsService.getRepositoryStats(true);
//
//        assertThat(stats).contains("Репозиторий: Основной");
//        assertThat(stats).contains("Всего задач: 2");
//        assertThat(stats).contains("Выполнено: 1");
//        assertThat(stats).contains("Активно: 1");
//    }
//
//    @Test
//    void getRepositoryStats_ForStub_ShouldReturnStats() {
//        String stats = statisticsService.getRepositoryStats(false);
//
//        assertThat(stats).contains("Репозиторий: Stub");
//        assertThat(stats).contains("Всего задач: 1");
//        assertThat(stats).contains("Выполнено: 0");
//        assertThat(stats).contains("Активно: 1");
//    }
//
//    @Test
//    void getRepositoryStats_EmptyPrimary_ShouldReturnZeroCounts() {
//        primaryRepository.getAll().forEach(task -> primaryRepository.deleteTask(task.getId()));
//
//        String stats = statisticsService.getRepositoryStats(true);
//
//        assertThat(stats).contains("Всего задач: 0");
//        assertThat(stats).contains("Выполнено: 0");
//        assertThat(stats).contains("Активно: 0");
//    }
//}
