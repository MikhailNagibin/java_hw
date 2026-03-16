package com.mipt.nagibinMikhail.toDoList.model;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель данных задачи.
 * Содержит основную информацию о задаче:
 * идентификатор(int id)
 * заголовок(String title) описание(String description)
 * статус выполнения (boolean completed)
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long id;
    private String title;
    private String description;
    private boolean completed;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDate dueDate;
    private Priority priority;
    @Builder.Default
    private Set<String> tags = new HashSet<>();
}
