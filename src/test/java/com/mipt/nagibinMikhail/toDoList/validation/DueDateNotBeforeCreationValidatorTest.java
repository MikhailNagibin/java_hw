package com.mipt.nagibinMikhail.toDoList.validation;

import com.mipt.nagibinMikhail.toDoList.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DueDateNotBeforeCreationValidatorTest {

    private DueDateNotBeforeCreationValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new DueDateNotBeforeCreationValidator();
    }

    @Test
    void isValid_WhenDueDateNull_ShouldReturnTrue() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(null);

        boolean valid = validator.isValid(dto, context);

        assertThat(valid).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    void isValid_WhenDueDateToday_ShouldReturnTrue() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now());

        boolean valid = validator.isValid(dto, context);

        assertThat(valid).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    void isValid_WhenDueDateFuture_ShouldReturnTrue() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now().plusDays(1));

        boolean valid = validator.isValid(dto, context);

        assertThat(valid).isTrue();
        verifyNoInteractions(context);
    }
}
