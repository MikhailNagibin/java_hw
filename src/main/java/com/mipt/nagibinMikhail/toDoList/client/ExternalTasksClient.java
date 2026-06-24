package com.mipt.nagibinMikhail.toDoList.client;

import com.mipt.nagibinMikhail.toDoList.dto.TaskCreateDto;
import com.mipt.nagibinMikhail.toDoList.dto.TaskDto;
import com.mipt.nagibinMikhail.toDoList.exception.ExternalApiException;
import com.mipt.nagibinMikhail.toDoList.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalTasksClient {

    private final RestClient restClient;

    public TaskDto createTask(TaskCreateDto taskDto) {
        log.debug("Creating task in external API: {}", taskDto.getTitle());

        ResponseEntity<TaskDto> response = restClient.post()
            .uri("/tasks")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .body(taskDto)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> {
                log.error("Error creating task: {} - {}", res.getStatusCode(), res.getBody());
                throw new ExternalApiException(
                    res.getStatusCode().value(),
                    "Failed to create task: " + res.getStatusCode()
                );
            })
            .toEntity(TaskDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            TaskDto task = response.getBody();
            log.debug("Task created successfully with id: {}", task != null ? task.getId() : null);
            return task;
        }

        throw new ExternalApiException(
            response.getStatusCode().value(),
            "Unexpected response when creating task"
        );
    }

    public TaskDto getTask(Long id) {
        log.debug("Getting task from external API: {}", id);

        try {
            ResponseEntity<TaskDto> response = restClient.get()
                .uri("/tasks/{id}", id)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String body = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    log.warn("Task not found or client error: {} - {}", res.getStatusCode(), body);

                    if (res.getStatusCode().value() == 404) {
                        throw new TaskNotFoundException("Task not found with id: " + id);
                    }
                    throw new ExternalApiException(
                        res.getStatusCode().value(),
                        "Client error: " + res.getStatusCode(),
                        body
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    String body = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    log.error("External API server error: {} - {}", res.getStatusCode(), body);
                    throw new ExternalApiException(
                        res.getStatusCode().value(),
                        "External API error: " + res.getStatusCode(),
                        body
                    );
                })
                .toEntity(TaskDto.class);

            TaskDto task = response.getBody();
            if (task == null) {
                throw new ExternalApiException(204, "Empty response from external API");
            }
            return task;

        } catch (TaskNotFoundException e) {
            throw e;
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting task: {}", e.getMessage(), e);
            throw new ExternalApiException("Error communicating with external API", e);
        }
    }

    public List<TaskDto> getTasks(Boolean completed, int limit) {
        log.debug("Getting tasks from external API: completed={}, limit={}", completed, limit);

        try {
            URI uri = UriComponentsBuilder.fromPath("/tasks")
                .queryParamIfPresent("completed", completed != null ? java.util.Optional.of(completed) : java.util.Optional.empty())
                .queryParam("limit", limit)
                .build()
                .toUri();

            ResponseEntity<List<TaskDto>> response = restClient.get()
                .uri(uri)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String body = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    log.error("Error getting tasks: {} - {}", res.getStatusCode(), body);
                    throw new ExternalApiException(
                        res.getStatusCode().value(),
                        "Failed to get tasks: " + res.getStatusCode(),
                        body
                    );
                })
                .toEntity(new ParameterizedTypeReference<List<TaskDto>>() {});

            List<TaskDto> tasks = response.getBody();
            if (tasks == null) {
                log.warn("Empty response when getting tasks");
                return List.of();
            }

            log.debug("Retrieved {} tasks from external API", tasks.size());
            return tasks;

        } catch (Exception e) {
            log.error("Unexpected error getting tasks: {}", e.getMessage(), e);
            throw new ExternalApiException("Error communicating with external API", e);
        }
    }

    public void deleteTask(Long id) {
        log.debug("Deleting task from external API: {}", id);

        try {
            ResponseEntity<Void> response = restClient.delete()
                .uri("/tasks/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String body = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    log.warn("Client error deleting task: {} - {}", res.getStatusCode(), body);

                    if (res.getStatusCode().value() == 404) {
                        throw new TaskNotFoundException("Task not found with id: " + id);
                    }
                    throw new ExternalApiException(
                        res.getStatusCode().value(),
                        "Client error: " + res.getStatusCode(),
                        body
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    String body = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    log.error("External API server error deleting task: {} - {}", res.getStatusCode(), body);
                    throw new ExternalApiException(
                        res.getStatusCode().value(),
                        "External API error: " + res.getStatusCode(),
                        body
                    );
                })
                .toBodilessEntity();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalApiException(
                    response.getStatusCode().value(),
                    "Failed to delete task: " + response.getStatusCode()
                );
            }

            log.debug("Task deleted successfully: {}", id);

        } catch (TaskNotFoundException e) {
            throw e;
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting task: {}", e.getMessage(), e);
            throw new ExternalApiException("Error communicating with external API", e);
        }
    }

    private String extractIdFromLocation(URI location) {
        if (location == null) {
            return null;
        }
        String path = location.getPath();
        if (path == null) {
            return null;
        }
        String[] segments = path.split("/");
        if (segments.length == 0) {
            return null;
        }
        return segments[segments.length - 1];
    }
}
