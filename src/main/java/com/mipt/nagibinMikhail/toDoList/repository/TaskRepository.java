package com.mipt.nagibinMikhail.toDoList.repository;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByCompleted(boolean completed);

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    List<Task> findByDueDateBefore(LocalDate date);

    long countByCompleted(boolean completed);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :today AND :nextWeek")
    List<Task> findTasksDueInNextWeek(@Param("today") LocalDate today, @Param("nextWeek") LocalDate nextWeek);

    @Query(value = "SELECT * FROM tasks WHERE due_date IS NOT NULL AND due_date <= CURRENT_DATE + INTERVAL '7 days'",
        nativeQuery = true)
    List<Task> findUpcomingTasksNative();

    @EntityGraph(attributePaths = {"attachments"})
    @Query("SELECT t FROM Task t")
    List<Task> findAllWithAttachments();

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.attachments")
    List<Task> findAllWithAttachmentsJoinFetch();

    @EntityGraph(attributePaths = {"attachments"})
    Optional<Task> findById(Integer id);

    @Query("UPDATE Task t SET t.completed = true WHERE t.id IN :ids")
    int markCompletedByIds(@Param("ids") List<Integer> ids);

    long countByIdIn(List<Integer> ids);
}