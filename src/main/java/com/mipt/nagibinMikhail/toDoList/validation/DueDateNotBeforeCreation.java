package com.mipt.nagibinMikhail.toDoList.validation;

import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DueDateNotBeforeCreation {
    String message() default "Due date cannot be before task creation date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
