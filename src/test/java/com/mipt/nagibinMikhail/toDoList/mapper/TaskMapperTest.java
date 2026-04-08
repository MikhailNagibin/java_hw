package com.mipt.nagibinMikhail.toDoList.mapper;

import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskResponseDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskUpdateDto;
import com.mipt.nagibinMikhail.toDoList.model.Priority;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void toEntity_ShouldMapCreateDtoToTask() {
        TaskCreateDto dto = TaskCreateDto.builder()
            .title("Test Task")
            .description("Description")
            .dueDate(LocalDate.of(2025, 12, 31))
            .priority(Priority.HIGH)
            .tags(Set.of("work", "urgent"))
            .build();

        TaskModel task = taskMapper.toEntity(dto);

        assertThat(task.getId()).isZero(); // игнорируется
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.isCompleted()).isFalse(); // константа
        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.getTags()).containsExactlyInAnyOrder("work", "urgent");
    }

    @Test
    void updateEntity_ShouldUpdateOnlyNonNullFields() {
        TaskModel task = TaskModel.builder()
            .id(1)
            .title("Old Title")
            .description("Old Description")
            .completed(true)
            .createdAt(LocalDateTime.now())
            .dueDate(LocalDate.now().minusDays(1))
            .priority(Priority.LOW)
            .tags(Set.of("old"))
            .build();

        TaskUpdateDto dto = TaskUpdateDto.builder()
            .title("New Title")
            .dueDate(LocalDate.now().plusDays(5))
            .priority(Priority.MEDIUM)
            .build();

        taskMapper.updateEntity(dto, task);

        assertThat(task.getTitle()).isEqualTo("New Title");
        assertThat(task.getDescription()).isEqualTo("Old Description"); // не менялось
        assertThat(task.isCompleted()).isTrue(); // не менялось
        assertThat(task.getCreatedAt()).isNotNull(); // не менялось
        assertThat(task.getDueDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(task.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(task.getTags()).containsExactly("old"); // не менялось
    }

    @Test
    void toResponseDto_ShouldMapTaskToDto() {
        LocalDateTime now = LocalDateTime.now();
        TaskModel task = TaskModel.builder()
            .id(10)
            .title("Task")
            .description("Desc")
            .completed(false)
            .createdAt(now)
            .dueDate(LocalDate.of(2025, 1, 1))
            .priority(Priority.LOW)
            .tags(Set.of("tag1", "tag2"))
            .build();

        TaskResponseDto dto = taskMapper.toResponseDto(task);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getTitle()).isEqualTo("Task");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.isCompleted()).isFalse();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getDueDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(dto.getPriority()).isEqualTo(Priority.LOW);
        assertThat(dto.getTags()).containsExactlyInAnyOrder("tag1", "tag2");
    }
}
