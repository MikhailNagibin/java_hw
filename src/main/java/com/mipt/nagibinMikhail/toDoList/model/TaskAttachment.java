package com.mipt.nagibinMikhail.toDoList.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachment {
    private Long id;
    private int taskId;
    private String fileName;
    private String storedFileName;
    private String contentType;
    private long size;

    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
