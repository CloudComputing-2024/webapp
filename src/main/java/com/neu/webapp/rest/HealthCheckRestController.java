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
                                     .header("Access-Control-Allow-Credentials", "true")
                                     .header("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept,Origin")
                                     .header("Access-Control-Allow-Methods", "*")
                                     .header("Access-Control-Allow-Origin", "*")
                                     .header("Cache-Control", "no-cache")
                                     .header("Content-Type", "application/octet-stream")
                                     .header("Expires", "-1")
                                     .header("X-Powered-By", "Express")
                                     .header("Pragma", "no-cache")
                                     .header("X-Content-Type-Options", "nosniff")
                                     .header("Allow", "GET")
                                     .build();
            }

            // check for request payload
            if (request.getContentLengthLong() > 0) {
                throw new UnexpectedPayloadException("Payload unexpected");
            }

            // respond with HTTP 200 OK status if database connection is successful
            return ResponseEntity.ok()
                                 .header("Access-Control-Allow-Credentials", "true")
                                 .header("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept,Origin")
                                 .header("Access-Control-Allow-Methods", "*")
                                 .header("Access-Control-Allow-Origin", "*")
                                 .header("Cache-Control", "no-cache")
                                 .header("Content-Type", "application/octet-stream")
                                 .header("Expires", "-1")
                                 .header("X-Powered-By", "Express")
                                 .header("Pragma", "no-cache")
                                 .header("X-Content-Type-Options", "nosniff")
                                 .header("Allow", "GET")
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
                             .header("Access-Control-Allow-Credentials", "true")
                             .header("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept,Origin")
                             .header("Access-Control-Allow-Methods", "*")
                             .header("Access-Control-Allow-Origin", "*")
                             .header("Cache-Control", "no-cache")
                             .header("Content-Type", "application/octet-stream")
                             .header("Expires", "-1")
                             .header("X-Powered-By", "Express")
                             .header("Pragma", "no-cache")
                             .header("X-Content-Type-Options", "nosniff")
                             .header("Allow", "GET")
                             .build();
    }

    // Not allowed OPTIONS request for "/healthz"
    @RequestMapping(value = "/healthz", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> handleOptionsForHealthz() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .header("Access-Control-Allow-Credentials", "true")
                             .header("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept,Origin")
                             .header("Access-Control-Allow-Methods", "*")
                             .header("Access-Control-Allow-Origin", "*")
                             .header("Cache-Control", "no-cache")
                             .header("Content-Type", "application/octet-stream")
                             .header("Expires", "-1")
                             .header("X-Powered-By", "Express")
                             .header("Pragma", "no-cache")
                             .header("X-Content-Type-Options", "nosniff")
                             .header("Allow", "GET")
                             .build();
    }
}
