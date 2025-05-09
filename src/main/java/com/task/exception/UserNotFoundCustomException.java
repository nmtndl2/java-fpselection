package com.task.exception;

public class UserNotFoundCustomException extends RuntimeException {
    public UserNotFoundCustomException(String message) {
        super(message);
    }
}
