package com.mipt.nagibinMikhail.toDoList.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Setter
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
