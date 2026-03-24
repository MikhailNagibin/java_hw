package com.mipt.nagibinMikhail.toDoList.controller;

import com.mipt.nagibinMikhail.toDoList.exception.TaskAttachmentNotFoundException;
import com.mipt.nagibinMikhail.toDoList.exception.TaskNotFoundException;
import com.mipt.nagibinMikhail.toDoList.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @Test
    void uploadAttachment_TaskNotFound_ShouldReturn404() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());

        doThrow(new TaskNotFoundException("Task not found with id: 99"))
            .when(attachmentService).storeAttachment(eq(99), any());

        mockMvc.perform(multipart("/api/tasks/99/attachments")
                .file(file))
            .andExpect(status().isNotFound());
    }

    @Test
    void downloadAttachment_NotFound_ShouldReturn404() throws Exception {
        doThrow(new TaskAttachmentNotFoundException("Attachment not found with id: 999"))
            .when(attachmentService).getAttachment(999L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/api/attachments/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteAttachment_NotFound_ShouldReturn404() throws Exception {
        doThrow(new TaskAttachmentNotFoundException("Attachment not found with id: 999"))
            .when(attachmentService).deleteAttachment(999L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .delete("/api/attachments/999"))
            .andExpect(status().isNotFound());
    }
}