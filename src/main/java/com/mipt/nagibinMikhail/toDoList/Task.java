package com.mipt.nagibinMikhail.toDoList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Модель данных задачи.
 * Содержит основную информацию о задаче:
 * идентификатор(int id)
 * заголовок(String title) описание(String description)
 * статус выполнения (boolean completed)
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Task {
    int id;
    String title;
    String description;
    boolean completed;
}
