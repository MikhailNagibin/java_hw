package com.mipt.nagibinMikhail.toDoList.dto;

import com.mipt.nagibinMikhail.toDoList.model.Priority;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Данные для создания задачи")
public class TaskCreateDto {
    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 100, groups = OnCreate.class)
    @Schema(description = "Заголовок задачи", example = "Купить продукты", minLength = 3, maxLength = 100)
    private String title;

    @Size(max = 500, groups = OnCreate.class)
    @Schema(description = "Описание задачи", maxLength = 500)
    private String description;

    @FutureOrPresent(groups = OnCreate.class)
    @Schema(description = "Срок выполнения", example = "2025-12-31")
    private LocalDate dueDate;

    @NotNull(groups = OnCreate.class)
    @Schema(description = "Приоритет", allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private Priority priority;

    @Size(max = 5, groups = OnCreate.class)
    @Schema(description = "Теги (макс. 5)", example = "[\"работа\", \"срочно\"]")
    private Set<String> tags;
}
