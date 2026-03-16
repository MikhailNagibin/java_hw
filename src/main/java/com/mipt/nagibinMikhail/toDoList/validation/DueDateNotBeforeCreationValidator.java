package com.mipt.nagibinMikhail.toDoList.validation;

import com.mipt.nagibinMikhail.toDoList.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        if (dto.getDueDate() == null) {
            return true;
        }

        LocalDate today = LocalDate.now();

        if (dto.getDueDate().isBefore(today)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Due date cannot be before today's date")
                .addPropertyNode("dueDate")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
