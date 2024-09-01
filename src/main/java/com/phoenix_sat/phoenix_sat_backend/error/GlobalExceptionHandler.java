package com.phoenix_sat.phoenix_sat_backend.error;

import com.amazonaws.services.simpleemail.model.AmazonSimpleEmailServiceException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND,exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException e) {
        StringBuilder stringBuilder = new StringBuilder();
        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> stringBuilder.append(String.format("%s : %s ",
                        ((FieldError) error).getField(), error.getDefaultMessage())));

        return new ErrorResponse(HttpStatus.BAD_REQUEST, stringBuilder.toString());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED,e.getMessage());
    }

    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNullValueException(NullValueException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handlePermissionDeniedException(PermissionDeniedException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(InvalidAnswerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidAnswerException(InvalidAnswerException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleResourceAlreadyExistException(ResourceAlreadyExistException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,e.getMessage());
    }

    @ExceptionHandler(EmailServiceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailServiceException(EmailServiceException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,e.getMessage());
    }

    @ExceptionHandler(RegistrationVerificationSessionIsExpiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleRegistrationVerificationSessionIsExpiredException(RegistrationVerificationSessionIsExpiredException e) {
        return new ErrorResponse(HttpStatus.FORBIDDEN,e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return new ErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE,e.getMessage());
    }


    @ExceptionHandler(AmazonSimpleEmailServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAmazonSESException(AmazonSimpleEmailServiceException ex) {
        String errorMessage = "Email sending failed: " + ex.getMessage();

        // Log the error if needed
         log.error("Amazon SES error: {}", ex.getMessage());

        if (ex.getErrorCode().equals("InvalidParameterValue")) {
            errorMessage = "Invalid email address provided.";
        }

        return new ErrorResponse(HttpStatus.BAD_REQUEST,errorMessage);
    }
}
