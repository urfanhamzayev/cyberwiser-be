package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

@Builder
public record UserRegisterResponse(String email,String fullName,String userId,Boolean isActive) {
}
