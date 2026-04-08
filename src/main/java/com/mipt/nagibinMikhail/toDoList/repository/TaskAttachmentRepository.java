package com.mipt.nagibinMikhail.toDoList.repository;

import com.mipt.nagibinMikhail.toDoList.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    // Исправлено: findByTask_Id вместо findByTaskId
    // Spring Data JPA ищет поле task.id, где task - это поле в сущности TaskAttachment
    List<TaskAttachment> findByTask_Id(Integer taskId);

    void deleteByTask_Id(Integer taskId);

    long countByTask_Id(Integer taskId);
}
