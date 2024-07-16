package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record CreateOrganizationRequest(@NotEmpty(message = "Organization name is required") String organizationName,

                                        @NotNull(message = "Organization type is required") OrganizationType organizationType) {
}
