package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.validator.Password;
import jakarta.validation.constraints.NotBlank;


public record RegistrationCompletionRequest(@Password
                                            @NotBlank(message = "Password should be defined")
                                            String password) {
}
