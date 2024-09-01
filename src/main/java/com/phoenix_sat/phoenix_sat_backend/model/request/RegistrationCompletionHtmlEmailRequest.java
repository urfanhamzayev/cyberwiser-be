package com.phoenix_sat.phoenix_sat_backend.model.request;

import lombok.Builder;

@Builder
public record RegistrationCompletionHtmlEmailRequest(String temporaryPassword,
                                                     String to,
                                                     String fullName,
                                                     String templateName) {
}
