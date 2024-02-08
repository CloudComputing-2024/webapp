package com.neu.webapp.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class HealthCheckRestController {

    @PersistenceContext
    private EntityManager entityManager;

    // "healthz" endpoint
    @GetMapping("/healthz")
    public ResponseEntity<String> checkHealth(HttpServletRequest request) throws SQLException {
        try {
            // check for database connection
            entityManager.createNativeQuery("SELECT 1").getSingleResult();

            // check for query parameters
            if (!request.getParameterMap().isEmpty()) {
                return ResponseEntity.badRequest()
                                     .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                     .header("Pragma", "no-cache")
                                     .header("X-Content-Type-Options", "nosniff")
                                     .build();
            }

            // check for request payload
            if (request.getContentLengthLong() > 0) {
                throw new UnexpectedPayloadException("Payload unexpected");
            }

            // responds with HTTP 200 OK status if database connection is successful
            return ResponseEntity.ok()
                                 .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                 .header("Pragma", "no-cache")
                                 .header("X-Content-Type-Options", "nosniff")
                                 .build();
        } catch (PersistenceException exception) {
            // throw to SQLException if database connection is unsuccessful
            throw new SQLException("Invalid Database Connection");
        }
    }

    // Not allowed HEAD request for "/healthz"
    @RequestMapping(value = "/healthz", method = RequestMethod.HEAD)
    public ResponseEntity<String> handleHeadForHealthz() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .header("Cache-Control", "no-cache, no-store, must-revalidate")
                             .header("Pragma", "no-cache")
                             .header("X-Content-Type-Options", "nosniff")
                             .build();
    }

    // Not allowed OPTIONS request for "/healthz"
    @RequestMapping(value = "/healthz", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> handleOptionsForHealthz() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .header("Cache-Control", "no-cache, no-store, must-revalidate")
                             .header("Pragma", "no-cache")
                             .header("X-Content-Type-Options", "nosniff")
                             .header("Allow", "GET")
                             .build();
    }
}
