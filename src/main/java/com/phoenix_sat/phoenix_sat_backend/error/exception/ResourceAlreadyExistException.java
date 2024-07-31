package com.phoenix_sat.phoenix_sat_backend.error.exception;

public class ResourceAlreadyExistException extends RuntimeException{
    public ResourceAlreadyExistException() {
    }

    public ResourceAlreadyExistException(String message) {
        super(message);
    }

    public ResourceAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
