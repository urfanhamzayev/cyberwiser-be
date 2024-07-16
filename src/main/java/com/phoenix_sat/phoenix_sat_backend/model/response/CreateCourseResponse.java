package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

@Builder
public record CreateCourseResponse(String id,
                                   String organizationId,
                                   String pictureUrl,
                                   String tags,
                                   String title,
                                   String description,
                                   String instructor,
                                   String duration,
                                   Integer availablePoint,
                                   Boolean isVisible) {
}
