package com.phoenix_sat.phoenix_sat_backend.model.request;

import jakarta.validation.constraints.NotEmpty;

public record CreateLectureRequest(@NotEmpty String title,
                                   @NotEmpty String duration,
                                   @NotEmpty String videoUrl,
                                   @NotEmpty String courseId) {
}
