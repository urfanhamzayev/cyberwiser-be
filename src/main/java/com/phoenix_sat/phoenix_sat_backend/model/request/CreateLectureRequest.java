package com.phoenix_sat.phoenix_sat_backend.model.request;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public record CreateLectureRequest(@NotEmpty String title,
                                   @NotEmpty String duration,
                                   @RequestParam MultipartFile video,
                                   @NotEmpty String courseId) {
}
