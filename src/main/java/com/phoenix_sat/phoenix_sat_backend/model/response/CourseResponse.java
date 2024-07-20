package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

@Builder
public record CourseResponse(String id,
                             String name,
                             String pictureUrl,
                             String tags,
                             String title,
                             String description,
                             String instructor,
                             String duration,
                             Boolean isVisible,
                             Integer availablePoint,
                             String organizationName) {
}
   // TODO: Need to add isCompleted field. user can see which one is completed in the full list of course
    // organizationName no need