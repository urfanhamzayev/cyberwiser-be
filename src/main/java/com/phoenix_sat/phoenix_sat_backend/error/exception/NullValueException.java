package com.phoenix_sat.phoenix_sat_backend.error.exception;

public class NullValueException extends RuntimeException{
    public NullValueException() {
    }

    public NullValueException(String message) {
        super(message);
    }

    public NullValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullValueException(Throwable cause) {
        super(cause);
    }
}
