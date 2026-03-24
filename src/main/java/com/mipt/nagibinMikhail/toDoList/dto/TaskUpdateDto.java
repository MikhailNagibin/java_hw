package com.mipt.nagibinMikhail.toDoList.dto;

import com.mipt.nagibinMikhail.toDoList.model.Priority;
import com.mipt.nagibinMikhail.toDoList.validation.DueDateNotBeforeCreation;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
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
@DueDateNotBeforeCreation(groups = OnUpdate.class)   // добавлено
public class TaskUpdateDto {
    @Size(min = 3, max = 100, groups = OnUpdate.class)
    private String title;

    @Size(max = 500, groups = OnUpdate.class)
    private String description;

    private Boolean completed;

    @FutureOrPresent(groups = OnUpdate.class)
    private LocalDate dueDate;

    private Priority priority;

    @Size(max = 5, groups = OnUpdate.class)
    private Set<String> tags;
}