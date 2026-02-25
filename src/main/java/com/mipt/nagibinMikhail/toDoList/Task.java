package com.mipt.nagibinMikhail.toDoList;

import lombok.*;


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
@AllArgsConstructor
public class Task {
    int id;
    String title;
    String description;
    boolean completed;
}
