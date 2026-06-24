package com.mipt.nagibinMikhail.toDoList.model;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskModel {
    private int id;
    private String title;
    private String description;
    private boolean completed;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDate dueDate;
    private Priority priority;

    @Builder.Default
    private Set<String> tags = new HashSet<>();
}
