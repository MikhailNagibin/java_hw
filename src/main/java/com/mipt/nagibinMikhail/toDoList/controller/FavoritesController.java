package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.dto.TaskResponseDto;
import com.mipt.nagibinMikhail.toDoList.mapper.TaskMapper;
import com.mipt.nagibinMikhail.toDoList.model.Task;
import com.mipt.nagibinMikhail.toDoList.service.TaskService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Integer taskId, HttpSession session) {
        Task task = taskService.readTask(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        Set<Integer> favorites = getFavoritesFromSession(session);
        favorites.add(taskId);
        session.setAttribute(FAVORITES_SESSION_KEY, favorites);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Integer taskId, HttpSession session) {
        Set<Integer> favorites = getFavoritesFromSession(session);
        favorites.remove(taskId);
        session.setAttribute(FAVORITES_SESSION_KEY, favorites);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        Set<Integer> favorites = getFavoritesFromSession(session);
        List<TaskResponseDto> favoriteTasks = favorites.stream()
            .map(taskService::readTask)
            .filter(task -> task != null)
            .map(taskMapper::toResponseDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteTasks);
    }

    @SuppressWarnings("unchecked")
    private Set<Integer> getFavoritesFromSession(HttpSession session) {
        Set<Integer> favorites = (Set<Integer>) session.getAttribute(FAVORITES_SESSION_KEY);
        if (favorites == null) {
            favorites = new java.util.HashSet<>();
        }
        return favorites;
    }
}