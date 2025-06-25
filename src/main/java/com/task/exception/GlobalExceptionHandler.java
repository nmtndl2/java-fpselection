package com.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String BAD_REQUEST = "Bad Request";


    // Handle User Already Exists Exception
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "User Registration Error");
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle User Not Found Exception
    @ExceptionHandler(UserNotFoundCustomException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundCustomException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "User Not Found Error");
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChamberRangeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(ChamberRangeNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "User Not Found Error");
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(ResourceNotExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, BAD_REQUEST);
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(AlreadyExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "Already exists");
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        response.put("errorCode", "VALIDATION_ERROR");
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(BadRequestException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, BAD_REQUEST);
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(DataNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, BAD_REQUEST);
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

//     Handle Other Generic Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "Internal Server Error");
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
