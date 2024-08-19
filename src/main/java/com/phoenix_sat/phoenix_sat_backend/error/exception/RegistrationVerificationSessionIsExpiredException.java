package com.phoenix_sat.phoenix_sat_backend.error.exception;

public class RegistrationVerificationSessionIsExpiredException extends RuntimeException{
    public RegistrationVerificationSessionIsExpiredException() {
    }

    public RegistrationVerificationSessionIsExpiredException(String message) {
        super(message);
    }

    public RegistrationVerificationSessionIsExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationVerificationSessionIsExpiredException(Throwable cause) {
        super(cause);
    }
}
