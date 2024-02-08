package com.neu.webapp.rest;

public class UnexpectedPayloadException extends RuntimeException {
    public UnexpectedPayloadException(String message) {
        super(message);
    }
}
