package com.mipt.nagibinMikhail.toDoList.mapper;

import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskResponseDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskUpdateDto;
import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    // ===== Entity ↔ Model (для TaskService) =====

    /**
     * Task (Entity) → TaskModel
     */
    @Mapping(target = "tags", expression = "java(task.getTagsSet())")
    TaskModel toModel(Task task);

    /**
     * List<Task> → List<TaskModel>
     */
    List<TaskModel> toModelList(List<Task> tasks);

    /**
     * TaskModel → Task (Entity)
     */
    @Mapping(target = "tags", expression = "java(model.getTags() != null ? String.join(\",\", model.getTags()) : \"\")")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskModel model);

    /**
     * Частичное обновление Entity из Model
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tags", expression = "java(model.getTags() != null ? String.join(\",\", model.getTags()) : task.getTags())")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Task task, TaskModel model);

    // ===== DTO ↔ Model (для TaskController) =====

    /**
     * TaskModel → TaskResponseDto
     */
    TaskResponseDto toResponseDto(TaskModel task);

    /**
     * List<TaskModel> → List<TaskResponseDto>
     */
    List<TaskResponseDto> toResponseDtoList(List<TaskModel> tasks);

    /**
     * TaskCreateDto → TaskModel
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    TaskModel toEntity(TaskCreateDto createDto);

    /**
     * TaskUpdateDto → TaskModel (частичное обновление)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TaskUpdateDto updateDto, @MappingTarget TaskModel task);
}