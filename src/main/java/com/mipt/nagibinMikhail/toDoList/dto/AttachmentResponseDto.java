package com.mipt.nagibinMikhail.toDoList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponseDto {
    private Long id;
    private String fileName;
    private long size;
    private LocalDateTime uploadedAt;
}
