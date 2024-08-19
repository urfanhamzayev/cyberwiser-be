package com.phoenix_sat.phoenix_sat_backend.event;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

public record RegistrationVerificationEvent(List<? extends User> users) {
}
