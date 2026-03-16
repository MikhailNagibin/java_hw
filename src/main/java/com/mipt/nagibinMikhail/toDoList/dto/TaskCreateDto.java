package com.mipt.nagibinMikhail.toDoList.dto;

import com.mipt.nagibinMikhail.toDoList.model.Priority;
import jakarta.validation.constraints.*;
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
    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 100, groups = OnCreate.class)
    private String title;

    @Size(max = 500, groups = OnCreate.class)
    private String description;

    @FutureOrPresent( groups = OnCreate.class)
    private LocalDate dueDate;

    @NotNull(groups = OnCreate.class)
    private Priority priority;

    @Size(max = 5, groups = OnCreate.class)
    private Set<String> tags;
}