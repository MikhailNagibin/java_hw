package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.dto.AttachmentResponseDto;
import com.mipt.nagibinMikhail.toDoList.exception.TaskAttachmentNotFoundException;
import com.mipt.nagibinMikhail.toDoList.exception.TaskNotFoundException;
import com.mipt.nagibinMikhail.toDoList.model.TaskAttachment;
import com.mipt.nagibinMikhail.toDoList.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @Test
    void uploadAttachment_ShouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());

        TaskAttachment attachment = TaskAttachment.builder()
            .id(1L)
            .fileName("test.txt")
            .size(7L)
            .uploadedAt(LocalDateTime.now())
            .build();

        when(attachmentService.storeAttachment(eq(1), any())).thenReturn(attachment);

        mockMvc.perform(multipart("/api/tasks/1/attachments")
                .file(file))
            .andExpect(status().isOk())
            .andExpect(header().string("X-API-Version", "2.0.0"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.fileName").value("test.txt"))
            .andExpect(jsonPath("$.size").value(7));
    }

    @Test
    void uploadAttachment_TaskNotFound_ShouldReturn404() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());

        when(attachmentService.storeAttachment(eq(99), any()))
            .thenThrow(new TaskNotFoundException("Task not found with id: 99"));

        mockMvc.perform(multipart("/api/tasks/99/attachments")
                .file(file))
            .andExpect(status().isNotFound());
    }

    @Test
    void getTaskAttachments_ShouldReturnList() throws Exception {
        TaskAttachment att1 = TaskAttachment.builder()
            .id(1L).fileName("doc.pdf").size(100L).uploadedAt(LocalDateTime.now()).build();
        TaskAttachment att2 = TaskAttachment.builder()
            .id(2L).fileName("image.png").size(500L).uploadedAt(LocalDateTime.now()).build();

        when(attachmentService.getAttachmentsByTaskId(1)).thenReturn(List.of(att1, att2));

        mockMvc.perform(get("/api/tasks/1/attachments"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-API-Version", "2.0.0"))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].fileName").value("doc.pdf"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].fileName").value("image.png"));
    }

    @Test
    void getTaskAttachments_EmptyList_ShouldReturnEmptyArray() throws Exception {
        when(attachmentService.getAttachmentsByTaskId(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks/1/attachments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void downloadAttachment_ShouldReturnResource() throws Exception {
        TaskAttachment attachment = TaskAttachment.builder()
            .id(1L)
            .fileName("test.txt")
            .storedFileName("abc123_test.txt")
            .build();

        when(attachmentService.getAttachment(1L)).thenReturn(attachment);

        Resource resource = mock(Resource.class);
        when(resource.getFilename()).thenReturn("test.txt");
        when(attachmentService.loadAsResource(1L)).thenReturn(resource);

        mockMvc.perform(get("/api/attachments/1"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\""))
            .andExpect(header().string("X-API-Version", "2.0.0"));
    }

    @Test
    void downloadAttachment_NotFound_ShouldReturn404() throws Exception {
        when(attachmentService.getAttachment(999L))
            .thenThrow(new TaskAttachmentNotFoundException("Attachment not found with id: 999"));

        mockMvc.perform(get("/api/attachments/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteAttachment_ShouldReturnNoContent() throws Exception {
        doNothing().when(attachmentService).deleteAttachment(1L);

        mockMvc.perform(delete("/api/attachments/1"))
            .andExpect(status().isNoContent())
            .andExpect(header().string("X-API-Version", "2.0.0"));
    }

    @Test
    void deleteAttachment_NotFound_ShouldReturn404() throws Exception {
        doThrow(new TaskAttachmentNotFoundException("Attachment not found with id: 999"))
            .when(attachmentService).deleteAttachment(999L);

        mockMvc.perform(delete("/api/attachments/999"))
            .andExpect(status().isNotFound());
    }
}