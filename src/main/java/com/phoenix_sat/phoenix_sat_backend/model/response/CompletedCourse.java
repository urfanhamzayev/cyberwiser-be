package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompletedCourse {
    private String courseId;
    private String courseName;
    private String courseTitle;
    private String coursePictureUrl;
    private String courseTags;
    private Date completedDate;
}
