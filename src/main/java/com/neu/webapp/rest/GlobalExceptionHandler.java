package com.neu.webapp.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles SQL exception
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleException(SQLException exception) {
        // responds with HTTP 503 Service Unavailable
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                             .header("Cache-Control", "no-cache, no-store, must-revalidate")
                             .header("Pragma", "no-cache")
                             .header("X-Content-Type-Options", "nosniff")
                             .build();
    }

    // Handles payload unexpected
    @ExceptionHandler(UnexpectedPayloadException.class)
    public ResponseEntity<String> handleException(UnexpectedPayloadException exception) {
        // responds with HTTP 400 Unexpected payload
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .header("Cache-Control", "no-cache, no-store, must-revalidate")
                             .header("Pragma", "no-cache")
                             .header("X-Content-Type-Options", "nosniff")
                             .build();
    }

    // Handles HTTP method not allowed exceptions
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleException(HttpRequestMethodNotSupportedException exception) {
        // responds with HTTP 405 Method Not Allowed
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .header("Cache-Control", "no-cache, no-store, must-revalidate")
                             .header("Pragma", "no-cache")
                             .header("X-Content-Type-Options", "nosniff")
                             .build();
    }
}
