package com.phoenix_sat.phoenix_sat_backend.error.exception;

public class EmailServiceException extends RuntimeException{
    public EmailServiceException() {
    }

    public EmailServiceException(String message) {
        super(message);
    }

    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailServiceException(Throwable cause) {
        super(cause);
    }
}
