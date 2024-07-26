package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(String userId,
                                  String organizationId,
                                  String email,
                                  String name,
                                  String pictureUrl) {
}
