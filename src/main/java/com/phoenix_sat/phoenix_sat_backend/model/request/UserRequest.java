package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import lombok.Builder;

import java.util.List;

@Builder
public record UserRequest(String organizationId,
                          List<RoleType> roleType,
                          String firstName,
                          String lastName,
                          String email) {
}
