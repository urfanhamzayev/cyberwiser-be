package com.phoenix_sat.phoenix_sat_backend.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@SuppressWarnings("unused")
public class ErrorResponse {
    private HttpStatus status;
    private Integer error;
    private List<String> messages;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;


    private ErrorResponse() {
        this.timestamp = new Date();
        this.messages = new ArrayList<>();
    }

    public ErrorResponse(HttpStatus status, String message) {
        this();
        this.status = status;
        this.messages.add(message);
        this.error = status.value();
    }

    public ErrorResponse(HttpStatus status, List<String> messages) {
        this();
        this.status = status;
        this.messages.addAll(messages);
        this.error = status.value();
    }
}
