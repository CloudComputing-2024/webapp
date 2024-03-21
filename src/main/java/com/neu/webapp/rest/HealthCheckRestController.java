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
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class HealthCheckRestController {

    @PersistenceContext
    private EntityManager entityManager;

    // "healthz" endpoint
    @GetMapping("/healthz")
    public ResponseEntity<String> checkHealth(HttpServletRequest request) throws SQLException {
        Logger logger = Logger.getLogger(this.getClass().getName());
        try {
            // check for database connection
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            logger.info("Database connection successful");

            // check for query parameters
            if (!request.getParameterMap().isEmpty()) {
                logger.warning("HTTP 400 Bad request - unexpected query parameters");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // check for request payload
            if (request.getContentLengthLong() > 0) {
                logger.severe("Unexpected payload received");
                throw new UnexpectedPayloadException("Payload unexpected");
            }

            logger.info("HTTP 200 OK - Request processed successfully");
            // respond with HTTP 200 OK status if database connection is successful
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (PersistenceException exception) {
            logger.severe("Invalid Database Connection");
            logger.log(Level.SEVERE,"Exception details", exception);
            // throw to SQLException if database connection is unsuccessful
            throw new SQLException("Invalid Database Connection");
        }
    }

    // Not allowed HEAD request for "/healthz"
    @RequestMapping(value = "/healthz", method = RequestMethod.HEAD)
    public ResponseEntity<String> handleHeadForHealthz() {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.warning("HTTP 405 Method Not Allowed");

        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Not allowed OPTIONS request for "/healthz"
    @RequestMapping(value = "/healthz", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> handleOptionsForHealthz() {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.warning("HTTP 405 Method Not Allowed");
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
