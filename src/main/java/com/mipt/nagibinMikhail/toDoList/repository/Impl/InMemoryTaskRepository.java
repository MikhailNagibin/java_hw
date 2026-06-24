//package com.mipt.nagibinMikhail.toDoList.repository.Impl;
//
//import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
//import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//@Primary
//public class InMemoryTaskRepository implements TaskRepository {
//    Random random = new Random();
//    private final Map<Integer, TaskModel> tasks = new HashMap<>();
//
//    @Override
//    public TaskModel readTask(int id) {
//        if (tasks.containsKey(id)) {
//            return tasks.get(id);
//        }
//        return null;
//    }
//
//    @Override
//    public TaskModel createTask(String title, String description, boolean completed) {
//        TaskModel task = TaskModel.builder()
//            .id(random.nextInt())
//            .title(title)
//            .description(description)
//            .completed(completed)
//            .build();
//        tasks.put(task.getId(), task);
//        return task;
//    }
//
//    @Override
//    public TaskModel updateTask(int id, String title, String description, boolean completed) {
//        TaskModel task = tasks.get(id);
//        task.setCompleted(completed);
//        task.setDescription(description);
//        task.setTitle(title);
//        tasks.put(task.getId(), task);
//        return task;
//    }
//
//    @Override
//    public void deleteTask(int id) {
//        tasks.remove(id);
//    }
//
//    @Override
//    public List<TaskModel> getAll() {
//        List<TaskModel> allTasks = new ArrayList<>();
//        for (int key : tasks.keySet()) {
//            allTasks.add(tasks.get(key));
//        }
//        return allTasks;
//    }
//}
