package com.tss.shorty.exception;

import com.tss.shorty.exception.error.BaseError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{// 1. Handle Security & JWT Authentication Errors (401 Unauthorized)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseError> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        BaseError error = BaseError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(ex.getMessage() != null ? ex.getMessage() : "Unauthorized access. Please provide a valid token.")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // 2. Handle Payload Validation Errors (@Valid in Controllers - 400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errorsMap = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorsMap.put(error.getField(), error.getDefaultMessage());
        }

        BaseError error = BaseError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Invalid request data")
                .path(request.getRequestURI())
                .errors(errorsMap)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3. Handle Business Rules & Duplicate Checks (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseError> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        BaseError error = BaseError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 4. Handle Missing Resources (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseError> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        BaseError error = BaseError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String customMessage = "Malformed JSON payload or invalid request body";

        // Extract detailed enum error if Jackson failed to deserialize an Enum field
        if (ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            customMessage = String.format("Invalid value '%s'. Allowed values are: %s",
                    ife.getValue(),
                    java.util.Arrays.toString(ife.getTargetType().getEnumConstants()));
        }

        BaseError error = BaseError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(customMessage)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
