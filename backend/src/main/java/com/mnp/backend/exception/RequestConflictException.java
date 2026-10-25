package com.mnp.backend.exception;

public class RequestConflictException extends RuntimeException {

    public RequestConflictException(String message) {
        super(message);
    }
}
