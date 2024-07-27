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
    private String userName;
    private String uerEmail;
    private List<InProgressCourses> inProgresses;
    private List<CompletedCourse> completed;

    // TODO: update as below structure
    //private List<CourseStatistics> completedCourse ( add one more field as response (COMPLETED/NOT_COMPLETED))
    //private List<CourseStatistics> notCompletedCourse
    // int countCompletedCourse
    // int countNotCompletedCourse
}
