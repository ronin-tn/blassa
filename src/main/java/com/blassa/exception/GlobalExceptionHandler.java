package com.blassa.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Phase 5: Concurrency & Optimistic Locking (409 Conflict)
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(OptimisticLockingFailureException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "SEAT_UPDATE_CONFLICT",
                "The ride was updated by another user. Please try again.");
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Email ou mot de passe incorrect");
    }
    // Phase 5: Idempotency - unique(ride_id, passenger_id) constraint (409
    // Conflict)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("bookings_ride_id_passenger_id_key")) {
            return buildErrorResponse(HttpStatus.CONFLICT, "PASSENGER_ALREADY_BOOKED",
                    "You have already booked a seat on this ride.");
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "DATABASE_CONSTRAINT_VIOLATION",
                "Invalid operation.");
    }

    // Business Logic Violations (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BUSINESS_RULE_VIOLATION", ex.getMessage());
    }

    // Generic RuntimeExceptions with context-aware status codes
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("not found")) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
            }
            if (message.contains("not authorized")) {
                return buildErrorResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
            }
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    // Auth errors
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND",
                "User not found: " + ex.getMessage());
    }

    // Validation errors (DTO field validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "VALIDATION_FAILED");
        response.put("message", "Request validation failed");
        response.put("details", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}
