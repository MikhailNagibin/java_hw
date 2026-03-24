package com.mipt.nagibinMikhail.toDoList.repository;

import com.mipt.nagibinMikhail.toDoList.model.TaskAttachment;
import java.util.List;
import java.util.Optional;

public interface TaskAttachmentRepository {
    TaskAttachment save(TaskAttachment attachment);
    Optional<TaskAttachment> findById(Long id);
    List<TaskAttachment> findByTaskId(int taskId);
    void deleteById(Long id);
    boolean existsById(Long id);
}