package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProgressReport {
    private String userId;
    private String userFullName;
    private String uerEmail;
    private List<CourseStatistics> completedCourses;
    private List<CourseStatistics> notCompletedCourses;
    private int countCompletedCourse;
    private int countNotCompletedCourse;
}
