package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.request.CreateCourseRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.CreateLectureRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.CreateQuizRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.QuizCompleteRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public CreateCourseResponse create(CreateCourseRequest courseRequest) {
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
    public QuizCompleteResponse completeQuiz(@RequestBody QuizCompleteRequest quizCompleteRequest) {
        return courseService.completeQuiz(quizCompleteRequest);
    }

    @PostMapping("/lecture")
    public LectureResponse addLecture(@RequestBody @Valid CreateLectureRequest createLectureRequest) {
        return courseService.addLecture(createLectureRequest);
    }

    @PostMapping("/quiz")
    public QuizResponse addQuiz(@RequestBody @Valid CreateQuizRequest createQuizRequest) {
        return courseService.addQuiz(createQuizRequest);
    }
}
