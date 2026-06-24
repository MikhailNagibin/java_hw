package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.dto.TaskDto;
import com.mipt.nagibinMikhail.toDoList.mapper.TaskMapper;
import com.mipt.nagibinMikhail.toDoList.model.TaskModel;
import com.mipt.nagibinMikhail.toDoList.service.TaskService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Slf4j
public class FavoritesController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

    @Value("${api.version:2.0.0}")
    private String apiVersion;

    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Integer taskId, HttpSession session) {
        log.info("Adding task {} to favorites", taskId);

        TaskModel task = taskService.readTask(taskId);
        if (task == null) {
            log.warn("Task not found: {}", taskId);
            return ResponseEntity.notFound().build();
        }

        Set<Integer> favorites = getFavoritesFromSession(session);
        favorites.add(taskId);
        session.setAttribute(FAVORITES_SESSION_KEY, favorites);

        return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Integer taskId, HttpSession session) {
        log.info("Removing task {} from favorites", taskId);

        Set<Integer> favorites = getFavoritesFromSession(session);
        favorites.remove(taskId);
        session.setAttribute(FAVORITES_SESSION_KEY, favorites);

        return ResponseEntity.noContent()
            .header("X-API-Version", apiVersion)
            .build();
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getFavorites(HttpSession session) {
        log.info("Getting all favorite tasks");

        Set<Integer> favorites = getFavoritesFromSession(session);
        List<TaskDto> favoriteTasks = favorites.stream()
            .map(taskService::readTask)
            .filter(task -> task != null)
            .map(taskMapper::toDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .header("X-Total-Count", String.valueOf(favoriteTasks.size()))
            .body(favoriteTasks);
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