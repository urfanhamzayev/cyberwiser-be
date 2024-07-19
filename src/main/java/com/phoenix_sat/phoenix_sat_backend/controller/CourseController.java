package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.entity.Course;
import com.phoenix_sat.phoenix_sat_backend.entity.CourseAssignment;
import com.phoenix_sat.phoenix_sat_backend.model.request.*;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public CourseResponse create(@RequestBody CreateCourseRequest courseRequest) {
        return courseService.create(courseRequest);
    }

    @GetMapping("{courseId}")
    public CourseContentResponse getCourseContent(@PathVariable String courseId) {
        return courseService.getCourseContent(courseId);
    }

    @GetMapping("quiz/{quizId}")
    public QuizQuestionsResponse getQuizQuestions(@PathVariable String quizId) {
        return courseService.getQuizQuestions(quizId);
    }

    @PostMapping("quiz/complete")
    public QuizCompleteResponse completeQuiz(@RequestBody @Valid QuizCompleteRequest quizCompleteRequest) {
        return courseService.completeQuiz(quizCompleteRequest);
    }

    @PostMapping("/lecture")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public LectureResponse addLecture(@RequestBody @Valid CreateLectureRequest createLectureRequest) {
        return courseService.addLecture(createLectureRequest);
    }

    @PostMapping("/quiz")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public QuizResponse addQuiz(@RequestBody @Valid CreateQuizRequest createQuizRequest) {
        return courseService.addQuiz(createQuizRequest);
    }

    @GetMapping
    public Page<CourseResponse> getCoursePage(@ParameterObject CourseFilterRequest courseFilterRequest,
                                                @ParameterObject Pageable pageable) {
        return courseService.getCoursePage(courseFilterRequest,pageable);
    }


    @PutMapping("/{courseId}/confirm")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public CourseResponse confirmCourseAssignment(@PathVariable String courseId) {
        return courseService.confirmCourseAssignment(courseId);
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public void delete(@PathVariable String courseId) {
        courseService.delete(courseId);
    }
}
