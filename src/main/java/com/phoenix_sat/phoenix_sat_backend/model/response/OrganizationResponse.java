package com.phoenix_sat.phoenix_sat_backend.model.response;

import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.mark.Create;
import com.phoenix_sat.phoenix_sat_backend.validator.Email;
import lombok.Builder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
public record OrganizationResponse(String organizationId,
                                   String organizationName,
                                   OrganizationType organizationType,
                                   String email,
                                   String description,
                                   String phoneNumber,
                                   Integer numEmployees,
                                   String country,
                                   String industry,
                                   String domain,
                                   String logoKeyName) {
}
