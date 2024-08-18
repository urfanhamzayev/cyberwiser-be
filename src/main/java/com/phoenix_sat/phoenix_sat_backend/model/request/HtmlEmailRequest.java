package com.phoenix_sat.phoenix_sat_backend.model.request;

import lombok.Builder;

@Builder
public record HtmlEmailRequest(String subject, String to, String name, String templateName) {
}
