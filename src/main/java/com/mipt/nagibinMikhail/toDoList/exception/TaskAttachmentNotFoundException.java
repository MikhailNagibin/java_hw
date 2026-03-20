package com.mipt.nagibinMikhail.toDoList.exception;

public class TaskAttachmentNotFoundException extends RuntimeException {
    public TaskAttachmentNotFoundException(String message) {
        super(message);
    }
}