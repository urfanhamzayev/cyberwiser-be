package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.request.*;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public CourseResponse create(@ModelAttribute @Valid CreateCourseRequest courseRequest) {
        return courseService.create(courseRequest);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public CourseResponse update(@RequestBody CourseUpdateRequest courseUpdateRequest) {
        return courseService.update(courseUpdateRequest);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public List<CourseAssignResponse> assign(@RequestBody @Valid CourseAssignRequest courseAssignRequest) {
        return courseService.assign(courseAssignRequest);
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

    @PostMapping(value = "/lecture", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public LectureResponse addLecture(@ModelAttribute @Valid CreateLectureRequest createLectureRequest) {
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

    @PutMapping("/{assignmentId}/confirm")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CourseResponse confirmCourseAssignment(@PathVariable String assignmentId) {
        return courseService.confirmCourseAssignment(assignmentId);
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

    @GetMapping("/assignments")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CourseAssignResponse> getAssignments() {
        return courseService.getAssignments();
    }

    @GetMapping("/{courseId}/report")
    public ResponseEntity<byte[]> generateCompletionReport(@PathVariable String courseId) {
        byte[] pdf = courseService.generateCompletionReport(courseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificate.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
