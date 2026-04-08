package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.exception.BulkUpdateException;
import com.mipt.nagibinMikhail.toDoList.model.Priority;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_ShouldPersistAndReturnTask() {
        TaskModel created = taskService.createTask("Test Task", "Test Description", false);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Test Task");
        assertThat(created.getDescription()).isEqualTo("Test Description");
        assertThat(created.isCompleted()).isFalse();
        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    void createTask_WithModel_ShouldPersistAndReturnTask() {
        TaskModel taskModel = TaskModel.builder()
            .title("Model Task")
            .description("Model Description")
            .priority(Priority.HIGH)
            .dueDate(LocalDate.now().plusDays(5))
            .build();

        TaskModel created = taskService.createTask(taskModel);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Model Task");
        assertThat(created.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void readTask_ShouldReturnTaskWhenExists() {
        TaskModel created = taskService.createTask("Read Test", "Description", false);
        TaskModel found = taskService.readTask(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getTitle()).isEqualTo("Read Test");
    }

    @Test
    void readTask_ShouldThrowExceptionWhenNotExists() {
        assertThatThrownBy(() -> taskService.readTask(999))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Task not found with id: 999");
    }

    @Test
    void updateTask_ShouldModifyExistingTask() {
        TaskModel created = taskService.createTask("Original", "Original Desc", false);

        TaskModel updated = taskService.updateTask(created.getId(), "Updated Title", "Updated Desc", true);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getDescription()).isEqualTo("Updated Desc");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void updateTask_WithModel_ShouldModifyExistingTask() {
        TaskModel created = taskService.createTask("Original", "Original Desc", false);

        TaskModel updateData = TaskModel.builder()
            .title("Updated via Model")
            .priority(Priority.HIGH)
            .build();

        TaskModel updated = taskService.updateTask(created.getId(), updateData);

        assertThat(updated.getTitle()).isEqualTo("Updated via Model");
        assertThat(updated.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(updated.getDescription()).isEqualTo("Original Desc");
    }

    @Test
    void deleteTask_ShouldRemoveTask() {
        TaskModel created = taskService.createTask("To Delete", "Desc", false);
        boolean deleted = taskService.deleteTask(created.getId());

        assertThat(deleted).isTrue();
        assertThat(taskRepository.existsById(created.getId())).isFalse();
    }

    @Test
    void deleteTask_ShouldReturnFalseWhenNotExists() {
        boolean deleted = taskService.deleteTask(999);
        assertThat(deleted).isFalse();
    }

    @Test
    void getAll_ShouldReturnAllTasks() {
        taskService.createTask("Task 1", "Desc 1", false);
        taskService.createTask("Task 2", "Desc 2", true);
        taskService.createTask("Task 3", "Desc 3", false);

        List<TaskModel> tasks = taskService.getAll();

        assertThat(tasks).hasSize(3);
        assertThat(tasks).extracting(TaskModel::getTitle)
            .containsExactlyInAnyOrder("Task 1", "Task 2", "Task 3");
    }

    @Test
    void getTasksByCompleted_ShouldReturnFilteredTasks() {
        taskService.createTask("Active 1", "Desc", false);
        taskService.createTask("Completed 1", "Desc", true);
        taskService.createTask("Active 2", "Desc", false);

        List<TaskModel> activeTasks = taskService.getTasksByCompleted(false);
        List<TaskModel> completedTasks = taskService.getTasksByCompleted(true);

        assertThat(activeTasks).hasSize(2);
        assertThat(completedTasks).hasSize(1);
    }

    @Test
    void bulkCompleteTasks_ShouldThrowExceptionAndRollback_WhenNonExistentId() {
        TaskModel task1 = taskService.createTask("Task 1", "Desc", false);
        TaskModel task2 = taskService.createTask("Task 2", "Desc", false);

        List<Integer> ids = List.of(task1.getId(), task2.getId(), 999);

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(ids))
            .isInstanceOf(BulkUpdateException.class)
            .hasMessageContaining("Not all tasks exist");

        TaskModel unchanged1 = taskService.readTask(task1.getId());
        TaskModel unchanged2 = taskService.readTask(task2.getId());

        assertThat(unchanged1.isCompleted()).isFalse();
        assertThat(unchanged2.isCompleted()).isFalse();
    }

    @Test
    void bulkCompleteTasks_ShouldHandleEmptyList() {
        taskService.bulkCompleteTasks(List.of());

        long count = taskRepository.count();
        assertThat(count).isZero();
    }
}