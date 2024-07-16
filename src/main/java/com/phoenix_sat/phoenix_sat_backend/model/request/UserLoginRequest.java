package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.validator.Email;
import com.phoenix_sat.phoenix_sat_backend.validator.Password;

public record UserLoginRequest(@Email(message = "Email invalid") String email,
                                String password) {
}
