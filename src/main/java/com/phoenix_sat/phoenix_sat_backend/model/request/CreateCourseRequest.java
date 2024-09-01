package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public record CreateCourseRequest(
        @NotEmpty String name,
        @RequestParam MultipartFile picture,
        @NotEmpty String tags,
        @NotEmpty String title,
        String description,
        @NotEmpty String instructor,
        @NotEmpty String duration,
        @NotNull Integer availablePoint
) {
}
