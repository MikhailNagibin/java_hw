package com.mipt.nagibinMikhail.toDoList.repository.Impl;

import com.mipt.nagibinMikhail.toDoList.model.TaskAttachment;
import com.mipt.nagibinMikhail.toDoList.repository.TaskAttachmentRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {
    private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(idGenerator.getAndIncrement());
        }
        storage.put(attachment.getId(), attachment);
        return attachment;
    }

    @Override
    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TaskAttachment> findByTaskId(int taskId) {
        return storage.values().stream()
            .filter(a -> a.getTaskId().equals(taskId))
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
