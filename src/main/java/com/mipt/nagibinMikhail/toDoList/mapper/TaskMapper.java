package com.mipt.nagibinMikhail.toDoList.mapper;

import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskResponseDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskUpdateDto;
import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    default TaskModel toEntity(TaskCreateDto createDto) {
        if (createDto == null) return null;
        TaskModel task = new TaskModel();
        task.setTitle(createDto.getTitle());
        task.setDescription(createDto.getDescription());
        task.setCompleted(createDto.getCompleted() != null ? createDto.getCompleted() : false);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }

    default TaskResponseDto toResponseDto(TaskModel task) {
        if (task == null) return null;
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId((long) task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    default void updateEntity(TaskUpdateDto updateDto, @MappingTarget TaskModel task) {
        if (updateDto == null || task == null) return;
        if (updateDto.getTitle() != null) task.setTitle(updateDto.getTitle());
        if (updateDto.getDescription() != null) task.setDescription(updateDto.getDescription());
        if (updateDto.getCompleted() != null) task.setCompleted(updateDto.getCompleted());
        task.setUpdatedAt(LocalDateTime.now());
    }

    default TaskDto toDto(TaskModel task) {
        if (task == null) return null;
        TaskDto dto = new TaskDto();
        dto.setId((long) task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    default TaskModel toEntity(TaskDto taskDto) {
        if (taskDto == null) return null;
        TaskModel task = new TaskModel();
        task.setId(taskDto.getId() != null ? taskDto.getId().intValue() : null);
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setCompleted(taskDto.getCompleted() != null ? taskDto.getCompleted() : false);
        task.setCreatedAt(taskDto.getCreatedAt() != null ? taskDto.getCreatedAt() : LocalDateTime.now());
        task.setUpdatedAt(taskDto.getUpdatedAt() != null ? taskDto.getUpdatedAt() : LocalDateTime.now());
        return task;
    }

    default TaskModel toModel(TaskCreateDto createDto) {
        return toEntity(createDto);
    }

    default TaskModel toModel(TaskDto taskDto) {
        return toEntity(taskDto);
    }

    default TaskModel toModel(Task task) {
        if (task == null) return null;
        TaskModel model = new TaskModel();
        model.setId(task.getId());
        model.setTitle(task.getTitle());
        model.setDescription(task.getDescription());
        model.setCompleted(task.isCompleted());
        model.setCreatedAt(task.getCreatedAt());
        model.setUpdatedAt(task.getUpdatedAt());
        return model;
    }

    default Task toEntity(TaskModel model) {
        if (model == null) return null;
        Task task = new Task();
        task.setId(model.getId());
        task.setTitle(model.getTitle());
        task.setDescription(model.getDescription());
        task.setCompleted(model.isCompleted());
        task.setCreatedAt(model.getCreatedAt());
        task.setUpdatedAt(model.getUpdatedAt());
        return task;
    }

    default void updateEntity(Task task, TaskModel model) {
        if (task == null || model == null) return;
        if (model.getTitle() != null) task.setTitle(model.getTitle());
        if (model.getDescription() != null) task.setDescription(model.getDescription());
        task.setCompleted(model.isCompleted());
        task.setUpdatedAt(LocalDateTime.now());
    }
}