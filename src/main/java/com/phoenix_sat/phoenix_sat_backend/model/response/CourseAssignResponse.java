package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

import java.util.Date;
@Builder
public record CourseAssignResponse(String assignmentId,
                                   String courseId,
                                   String courseName,
                                   String organizationName,
                                   String organizationId,
                                   Date assignedDate,
                                   Boolean confirmed
                                   ) {
}
