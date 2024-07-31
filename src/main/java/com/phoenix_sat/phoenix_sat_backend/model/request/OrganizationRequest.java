package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.mark.Create;
import com.phoenix_sat.phoenix_sat_backend.validator.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record OrganizationRequest(
        @NotEmpty( message = "Organization name is required") String organizationName,
        @NotNull(message = "Organization type is required") OrganizationType organizationType,
        @Email String email,
        @NotEmpty String description,
        @NotEmpty String phoneNumber,
        Integer numEmployees,
        String country,
        String industry,
        @NotEmpty String domain
) {
}

