package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.model.request.*;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    CourseResponse create(CreateCourseRequest courseRequest);

    CourseContentResponse getCourseContent(String courseId);

    QuizQuestionsResponse getQuizQuestions(String quizId);

    QuizCompleteResponse completeQuiz(QuizCompleteRequest quizCompleteRequest);

    LectureResponse addLecture(CreateLectureRequest createLectureRequest);

    QuizResponse addQuiz(CreateQuizRequest createQuizRequest);

    Page<CourseResponse> getCoursePage(CourseFilterRequest courseFilterRequest, Pageable pageable);

    CourseResponse confirmCourseAssignment(String assignmentId);

    void delete(String courseId);

    LectureResponse completeLecture(String lectureId);

    List<CourseAssignResponse> assign(CourseAssignRequest courseAssignRequest);

    List<CourseAssignResponse> getAssignments();

    CourseResponse update(CourseUpdateRequest courseUpdateRequest);
}


