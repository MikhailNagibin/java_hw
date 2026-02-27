package com.mipt.nagibinMikhail.toDoList.repository.Impl;

import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {
    Random random = new Random();
    private final Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public Task readTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Task createTask(String title, String description, boolean completed) {
        Task task = new Task(random.nextInt(), title, description, completed);
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
