package com.phoenix_sat.phoenix_sat_backend.error.exception;

public class InvalidAnswerException extends RuntimeException{
    public InvalidAnswerException() {
    }

    public InvalidAnswerException(String message) {
        super(message);
    }

    public InvalidAnswerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAnswerException(Throwable cause) {
        super(cause);
    }
}
