package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.exception.TaskAttachmentNotFoundException;
import com.mipt.nagibinMikhail.toDoList.exception.TaskNotFoundException;
import com.mipt.nagibinMikhail.toDoList.model.TaskAttachment;
import com.mipt.nagibinMikhail.toDoList.repository.TaskAttachmentRepository;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Task not found with id: " + taskId);
        }

        try {
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetLocation = uploadDir.resolve(storedFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            TaskAttachment attachment = TaskAttachment.builder()
                .taskId(taskId)
                .fileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

            log.info("File stored successfully: {}", storedFileName);
            return attachmentRepository.save(attachment);

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new TaskAttachmentNotFoundException("Attachment not found with id: " + attachmentId));
    }

    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = Paths.get(uploadPath).resolve(attachment.getStoredFileName());

        if (!Files.exists(filePath)) {
            throw new TaskAttachmentNotFoundException("File not found for attachment id: " + attachmentId);
        }

        return new FileSystemResource(filePath.toFile());
    }

    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        try {
            Path filePath = Paths.get(uploadPath).resolve(attachment.getStoredFileName());
            Files.deleteIfExists(filePath);
            attachmentRepository.deleteById(attachmentId);
            log.info("File deleted successfully: {}", attachment.getStoredFileName());
        } catch (IOException e) {
            log.error("Failed to delete file", e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    public List<TaskAttachment> getAttachmentsByTaskId(int taskId) {
        if (taskRepository.readTask(taskId) == null) {
            throw new IllegalArgumentException();
        }
        return attachmentRepository.findByTaskId(taskId);
    }
}
