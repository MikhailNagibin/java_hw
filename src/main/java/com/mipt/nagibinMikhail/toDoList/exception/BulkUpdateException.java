package com.mipt.nagibinMikhail.toDoList.exception;

public class BulkUpdateException extends RuntimeException {

    public BulkUpdateException(String message) {
        super(message);
    }

    public BulkUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}