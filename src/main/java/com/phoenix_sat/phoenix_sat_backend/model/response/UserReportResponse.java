package com.phoenix_sat.phoenix_sat_backend.model.response;

import org.springframework.http.HttpHeaders;

public record UserReportResponse(byte[] pdf, HttpHeaders headers) {
}
