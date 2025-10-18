package com.phoenix_sat.phoenix_sat_backend.error.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralException extends RuntimeException {

    private final int errorCode;
    private final HttpStatus status;

    public GeneralException(String message, int errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    // Common shortcut constructor for 404 or simple error
    public GeneralException(String message) {
        this(message, 404, HttpStatus.NOT_FOUND);
    }
}