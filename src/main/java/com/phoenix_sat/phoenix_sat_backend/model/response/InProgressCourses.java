package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InProgressCourses {
    private String courseId;
    private String courseName;
    private String courseTitle;
    private String coursePictureUrl;
    private String courseTags;
    private Integer progressPercentage;

}
