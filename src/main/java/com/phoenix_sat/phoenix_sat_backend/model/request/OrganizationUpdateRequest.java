package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.validator.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrganizationUpdateRequest(String organizationName,
                                        @Email String email,
                                        String description,
                                        String phoneNumber,
                                        Integer numEmployees,
                                        String country,
                                        String industry,
                                        String domain) {
}
