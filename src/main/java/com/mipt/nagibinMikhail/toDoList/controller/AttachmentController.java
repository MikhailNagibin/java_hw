package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.dto.AttachmentResponseDto;
import com.mipt.nagibinMikhail.toDoList.model.TaskAttachment;
import com.mipt.nagibinMikhail.toDoList.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<AttachmentResponseDto> uploadAttachment(
        @PathVariable Long taskId,
        @RequestParam("file") MultipartFile file) {

        TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);

        AttachmentResponseDto response = AttachmentResponseDto.builder()
            .id(attachment.getId())
            .fileName(attachment.getFileName())
            .size(attachment.getSize())
            .uploadedAt(attachment.getUploadedAt())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + attachment.getFileName() + "\"")
            .body(resource);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> getTaskAttachments(@PathVariable int taskId) {
        List<TaskAttachment> attachments = attachmentService.getAttachmentsByTaskId(taskId);

        List<AttachmentResponseDto> response = attachments.stream()
            .map(a -> AttachmentResponseDto.builder()
                .id(a.getId())
                .fileName(a.getFileName())
                .size(a.getSize())
                .uploadedAt(a.getUploadedAt())
                .build())
            .toList();

        return ResponseEntity.ok(response);
    }
}
