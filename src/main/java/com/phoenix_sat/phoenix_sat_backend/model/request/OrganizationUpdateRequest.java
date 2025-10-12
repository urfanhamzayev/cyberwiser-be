package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.validator.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public record OrganizationUpdateRequest( @NotEmpty( message = "Organization name is required") String organizationName,
                                         @Email String email,
                                         @NotEmpty String description,
                                         @NotEmpty String phoneNumber,
                                         Integer numEmployees,
                                         String countryCode,
                                         String industry) {
}
