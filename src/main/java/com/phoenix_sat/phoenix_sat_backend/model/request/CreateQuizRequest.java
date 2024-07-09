package com.phoenix_sat.phoenix_sat_backend.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateQuizRequest(@NotEmpty String courseId,
                                List<QuestionRequest> questionRequestList,
                                @NotNull Integer numberOfQuestions,
                                @NotEmpty String quizTitle,
                                @NotEmpty String quizSectionTitle) {
}
