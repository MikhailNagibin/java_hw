package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.mapper.TaskMapper;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void updateTask_shouldUpdateExistingTask() {
        int taskId = 1;
        String newTitle = "Updated";
        String newDesc = "Updated desc";
        boolean completed = true;

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Test");
        existingTask.setDescription("Desc");
        existingTask.setCompleted(false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle(newTitle);
        updatedTask.setDescription(newDesc);
        updatedTask.setCompleted(completed);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskModel updatedModel = new TaskModel();
        updatedModel.setId(taskId);
        updatedModel.setTitle(newTitle);
        updatedModel.setDescription(newDesc);
        updatedModel.setCompleted(completed);
        when(taskMapper.toModel(updatedTask)).thenReturn(updatedModel);

        TaskModel result = taskService.updateTask(taskId, newTitle, newDesc, completed);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(newTitle);
        assertThat(result.isCompleted()).isTrue();

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertThat(savedTask.getTitle()).isEqualTo(newTitle);
    }
}
