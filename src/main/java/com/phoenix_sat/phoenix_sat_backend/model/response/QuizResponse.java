package com.phoenix_sat.phoenix_sat_backend.model.response;

import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import lombok.Builder;

@Builder
public record QuizResponse(String quizId,
                           String title,
                           Integer numberOfQuestions,
                           ContentType contentType,
                           Boolean isCompleted
) {
}