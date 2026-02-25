package com.mipt.nagibinMikhail.toDoList.repository;

import com.mipt.nagibinMikhail.toDoList.Task;


/**
 * интерфейс репозитория для работы с задачами.
 * Определяет основные CRUD операции.
 *
 * createTask(String title, String description, boolean completed) - создание таски. На вход принимает данные о ней
 *
 * readTask(int taskId) - получение такси по её id.
 *
 * updateTask(int id, String title, String description, boolean completed) - обновление таски. На вход принимает все данные о ней,
 *
 * deleteTask(int taskId) - удаление таски по её id
 */
public interface TaskRepository {
    Task createTask(String title, String description, boolean completed);
    Task readTask(int taskId);
    Task updateTask(int id, String title, String description, boolean completed);
    void deleteTask(int taskId);
}
