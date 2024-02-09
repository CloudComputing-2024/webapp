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
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Handles payload unexpected
    @ExceptionHandler(UnexpectedPayloadException.class)
    public ResponseEntity<String> handleException(UnexpectedPayloadException exception) {
        // responds with HTTP 400 Unexpected payload
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Handles HTTP method not allowed exceptions
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleException(HttpRequestMethodNotSupportedException exception) {
        // responds with HTTP 405 Method Not Allowed
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handles IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
