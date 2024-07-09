package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

@Builder
public record OrganizationResponse(String organizationId,
                                   String organizationName) {
}
