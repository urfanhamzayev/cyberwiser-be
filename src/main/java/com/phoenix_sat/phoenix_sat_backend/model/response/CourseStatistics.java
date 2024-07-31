package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Builder
public record CourseStatistics(String courseId,
                               String courseName,
                               String courseTitle,
                               String coursePictureUrl,
                               String courseTags,
                               Date completedDate,
                               Integer progressPercentage,
                               Boolean isCompleted) {
}
