package com.phoenix_sat.phoenix_sat_backend.model.response;

import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import lombok.Builder;

@Builder
public record LectureResponse(String lectureId,
                              String title,
                              String duration,
                              String videoUrl,
                              ContentType contentType,
                              Boolean isCompleted) {
}
