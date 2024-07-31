package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.validator.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;


public record UserRegisterRequest(@NotEmpty String firstName,
                                  @NotEmpty String lastName,
                                  @Email String email,
                                  @Password String password,
                                  String pictureUrl
) {
}
