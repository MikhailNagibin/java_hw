package com.mipt.nagibinMikhail.toDoList.repository.Impl;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StubTaskRepository implements TaskRepository {
    Random random = new Random();


    public final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    int maxId;

    public  StubTaskRepository() {
        tasks.put(1, Task.builder()
            .id(random.nextInt())
            .title("title")
            .description("description")
            .completed(false)
            .build());
        maxId = 1;
    }

    @Override
    public Task readTask(int id) {
        return tasks.get(id);
    }

    @Override
    public Task createTask(String title, String description, boolean completed) {
        Task task = Task.builder()
            .id(random.nextInt())
            .title(title)
            .description(description)
            .completed(completed)
            .build();
        tasks.put(task.getId(), task);
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

    @Override
    public List<Task> getAll() {
        List<Task> allTasks = new ArrayList<>();
        for (int key : tasks.keySet()) {
            allTasks.add(tasks.get(key));
        }
        return allTasks;
    }
}
