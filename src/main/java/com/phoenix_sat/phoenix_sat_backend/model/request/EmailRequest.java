package com.phoenix_sat.phoenix_sat_backend.model.request;

public record EmailRequest(String to, String subject, String body) {
}
