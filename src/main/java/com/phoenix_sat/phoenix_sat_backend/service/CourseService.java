package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.entity.Course;
import com.phoenix_sat.phoenix_sat_backend.model.CreatePartRequest;
import com.phoenix_sat.phoenix_sat_backend.model.QuizRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.PartResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.QuizResponse;
import com.phoenix_sat.phoenix_sat_backend.repository.CourseRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.OrganizationRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.PartRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final OrganizationRepository organizationRepository;
    private final CourseRepository courseRepository;
    private final PartRepository partRepository;
    private final QuizRepository quizRepository;



    public PartResponse addPart(CreatePartRequest partRequest) {
        return null;
    }

    public QuizResponse addQuiz(QuizRequest quizRequest) {
        return null;
    }
}
