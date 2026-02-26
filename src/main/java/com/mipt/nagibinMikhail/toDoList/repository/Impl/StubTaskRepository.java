package com.mipt.nagibinMikhail.toDoList.repository.Impl;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;

import java.util.HashMap;
import java.util.Map;

public class StubTaskRepository implements TaskRepository {
    public final Map<Integer, Task> tasks = new HashMap<>();
    int maxId;

    public  StubTaskRepository() {
        tasks.put(1, new Task(1, "title", "description", false));
        maxId = 1;
    }

    @Override
    public Task readTask(int id) {
        return tasks.get(id);
    }

    @Override
    public Task createTask(String title, String description, boolean completed) {
        Task task = new Task(++maxId, title, description, completed);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(int id, String title, String description, boolean completed) {
        Task task = tasks.get(id);
        task.setCompleted(completed);
        task.setDescription(description);
        task.setTitle(title);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

}
