package com.mipt.nagibinMikhail.toDoList.dto;

import com.mipt.nagibinMikhail.toDoList.model.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDto {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private Set<String> tags;
}