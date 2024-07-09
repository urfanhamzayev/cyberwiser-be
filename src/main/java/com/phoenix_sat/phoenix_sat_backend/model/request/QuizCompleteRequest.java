package com.phoenix_sat.phoenix_sat_backend.model.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuizCompleteRequest(@NotEmpty String quizId,
                                  List<UserAnswers> userAnswers) {
}
