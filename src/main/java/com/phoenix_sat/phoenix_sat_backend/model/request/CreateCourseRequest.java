package com.phoenix_sat.phoenix_sat_backend.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateCourseRequest(
        String name,
        String pictureUrl,
        String tags,
        String title,
        String description,
        String instructor,
        String duration,
        Integer availablePoint
) {
}
