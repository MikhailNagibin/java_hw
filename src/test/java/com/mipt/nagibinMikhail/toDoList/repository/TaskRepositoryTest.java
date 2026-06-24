package com.mipt.nagibinMikhail.toDoList.repository;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.entity.TaskAttachment;
import com.mipt.nagibinMikhail.toDoList.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        // Очищаем перед каждым тестом
        entityManager.getEntityManager().createQuery("DELETE FROM Task").executeUpdate();

        task1 = Task.builder()
            .title("Task 1")
            .description("Description 1")
            .completed(false)
            .priority(Priority.HIGH)
            .dueDate(LocalDate.now().plusDays(3))
            .build();

        task2 = Task.builder()
            .title("Task 2")
            .description("Description 2")
            .completed(true)
            .priority(Priority.MEDIUM)
            .dueDate(LocalDate.now().plusDays(10))
            .build();

        task3 = Task.builder()
            .title("Task 3")
            .description("Description 3")
            .completed(false)
            .priority(Priority.LOW)
            .dueDate(LocalDate.now().plusDays(5))
            .build();

        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();
    }

    @Test
    void save_ShouldPersistTask() {
        Task newTask = Task.builder()
            .title("New Task")
            .description("New Description")
            .completed(false)
            .priority(Priority.MEDIUM)
            .build();

        Task saved = taskRepository.save(newTask);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("New Task");
        assertThat(taskRepository.count()).isEqualTo(4);
    }

    @Test
    void findById_ShouldReturnTaskWhenExists() {
        Task found = taskRepository.findById(task1.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Task 1");
        assertThat(found.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        boolean exists = taskRepository.findById(999).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    void findAll_ShouldReturnAllTasks() {
        List<Task> tasks = taskRepository.findAll();

        assertThat(tasks).hasSize(3);
        assertThat(tasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Task 1", "Task 2", "Task 3");
    }

    @Test
    void findByCompleted_ShouldReturnOnlyCompletedTasks() {
        List<Task> completedTasks = taskRepository.findByCompleted(true);

        assertThat(completedTasks).hasSize(1);
        assertThat(completedTasks.get(0).getTitle()).isEqualTo("Task 2");
    }

    @Test
    void findByCompleted_ShouldReturnOnlyActiveTasks() {
        List<Task> activeTasks = taskRepository.findByCompleted(false);

        assertThat(activeTasks).hasSize(2);
        assertThat(activeTasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Task 1", "Task 3");
    }

    @Test
    void findByCompletedAndPriority_ShouldReturnFilteredTasks() {
        List<Task> highPriorityActive = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);

        assertThat(highPriorityActive).hasSize(1);
        assertThat(highPriorityActive.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void findByDueDateBefore_ShouldReturnTasksWithEarlyDueDate() {
        LocalDate threshold = LocalDate.now().plusDays(7);
        List<Task> tasks = taskRepository.findByDueDateBefore(threshold);

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Task 1", "Task 3");
    }

    @Test
    void findTasksDueInNextWeek_ShouldReturnTasksDueWithin7Days() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        List<Task> tasks = taskRepository.findTasksDueInNextWeek(today, nextWeek);

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Task 1", "Task 3");
    }

    @Test
    void countByCompleted_ShouldReturnCorrectCount() {
        long completedCount = taskRepository.countByCompleted(true);
        long activeCount = taskRepository.countByCompleted(false);

        assertThat(completedCount).isEqualTo(1);
        assertThat(activeCount).isEqualTo(2);
    }

    @Test
    void countByIdIn_ShouldReturnCorrectCount() {
        List<Integer> ids = List.of(task1.getId(), task2.getId(), task3.getId());
        long count = taskRepository.countByIdIn(ids);

        assertThat(count).isEqualTo(3);
    }

    @Test
    void countByIdIn_WithNonExistentId_ShouldReturnPartialCount() {
        List<Integer> ids = List.of(task1.getId(), 999);
        long count = taskRepository.countByIdIn(ids);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void delete_ShouldRemoveTask() {
        taskRepository.deleteById(task1.getId());
        entityManager.flush();

        boolean exists = taskRepository.existsById(task1.getId());
        assertThat(exists).isFalse();
        assertThat(taskRepository.count()).isEqualTo(2);
    }

    @Test
    void existsById_ShouldReturnTrueWhenExists() {
        boolean exists = taskRepository.existsById(task1.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void updateTask_ShouldModifyFields() {
        Task task = taskRepository.findById(task1.getId()).get();
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setCompleted(true);
        task.setPriority(Priority.LOW);

        taskRepository.save(task);
        entityManager.flush();
        entityManager.clear();

        Task updated = taskRepository.findById(task1.getId()).get();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.isCompleted()).isTrue();
        assertThat(updated.getPriority()).isEqualTo(Priority.LOW);
    }
}
