package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.entity.Part;
import com.phoenix_sat.phoenix_sat_backend.model.CreatePartRequest;
import com.phoenix_sat.phoenix_sat_backend.model.QuizRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.PartResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.QuizResponse;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/part")
    public PartResponse addPart(@RequestBody CreatePartRequest partRequest){
        return courseService.addPart(partRequest);
    }

    @PostMapping("/quiz")
    public QuizResponse addQuiz(@RequestBody QuizRequest quizRequest){
        return courseService.addQuiz(quizRequest);
    }
}
