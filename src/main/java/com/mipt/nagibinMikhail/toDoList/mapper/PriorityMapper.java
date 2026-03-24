package com.mipt.nagibinMikhail.toDoList.mapper;

import com.mipt.nagibinMikhail.toDoList.model.Priority;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class PriorityMapper {

    public String priorityToString(Priority priority) {
        return priority != null ? priority.name() : null;
    }

    public Priority stringToPriority(String priority) {
        return priority != null ? Priority.valueOf(priority) : null;
    }
}
