package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.request.*;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    // TODO:
    // 1) User can only see their confirmed course +
    // 2)  Admin can see only his course, assigned by super admin course both (comfirmed/not comfirmed) +
    // 3) Super admin can see only main course +
    // 4) Admin can create a course /edit a course/delete a course
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
        return courseService.getCoursePage(courseFilterRequest, pageable);
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

    @PutMapping("/{lectureId}/complete")
    public LectureResponse completeLecture(@PathVariable String lectureId) {
        return courseService.completeLecture(lectureId);
    }

}
