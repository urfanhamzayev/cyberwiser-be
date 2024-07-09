package com.phoenix_sat.phoenix_sat_backend.mapper;

import com.phoenix_sat.phoenix_sat_backend.entity.Quiz;
import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import com.phoenix_sat.phoenix_sat_backend.model.response.QuizResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;
@Component
public class QuizResponseMapper implements Function<Quiz, QuizResponse> {
    @Override
    public QuizResponse apply(Quiz quiz) {
        return QuizResponse.builder()
                .title(quiz.getTitle())
                .quizId(quiz.getId())
                .numberOfQuestions(quiz.getNumberOfQuestions())
                .contentType(ContentType.QUIZ)
                .isCompleted(false)
                .build();
    }
}
