package com.tss.shorty.exception;

import com.tss.shorty.exception.error.BaseError;
import com.tss.shorty.exception.error.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Security & JWT Authentication Errors (401 Unauthorized)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseError> handleAuthenticationException(Exception ex) {
        BaseError error = new BaseError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // 2. Handle Payload Validation Errors (@Valid in Controllers - 400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorsMap = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorsMap.put(error.getField(), error.getDefaultMessage());
        }

        ValidationError error = new ValidationError(HttpStatus.BAD_REQUEST.value(), errorsMap);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3. Handle Business Rules & Duplicate Checks (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseError> handleIllegalArgumentException(IllegalArgumentException ex) {
        BaseError error = new BaseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 4. Handle Missing Resources (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        BaseError error =new BaseError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String customMessage = "Malformed JSON payload or invalid request body";

        // Extract detailed enum error if Jackson failed to deserialize an Enum field
        if (ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            customMessage = String.format("Invalid value '%s'. Allowed values are: %s",
                    ife.getValue(),
                    java.util.Arrays.toString(ife.getTargetType().getEnumConstants()));
        }

        BaseError error = new BaseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<BaseError> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        BaseError error = new BaseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseError> handleAccessDeniedExceptionException(AccessDeniedException ex) {
        BaseError error = new BaseError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        BaseError error = new BaseError(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseError> handleGenericException(Exception ex) {
        BaseError error = new BaseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
