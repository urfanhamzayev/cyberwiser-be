package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

@Builder
public record QuizResponse(String id,
                           String title,
                           Integer numberOfQuestions
) {
}