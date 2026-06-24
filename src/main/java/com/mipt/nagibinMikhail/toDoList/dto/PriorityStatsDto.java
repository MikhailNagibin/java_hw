package com.mipt.nagibinMikhail.toDoList.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriorityStatsDto {
    private String priority;
    private long count;
}
