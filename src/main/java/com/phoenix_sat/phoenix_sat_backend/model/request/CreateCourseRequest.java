package com.phoenix_sat.phoenix_sat_backend.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateCourseRequest(
        @NotEmpty String name,
        @NotEmpty String pictureUrl,
        @NotEmpty String tags,
        @NotEmpty String title,
        String description,
        @NotEmpty String instructor,
        @NotEmpty String duration,
        @NotNull Integer availablePoint
) {
}
