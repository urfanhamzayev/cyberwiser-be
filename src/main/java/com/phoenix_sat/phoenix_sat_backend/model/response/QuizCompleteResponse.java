package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;

@Builder
public record QuizCompleteResponse(Integer rate,
                                   String quizId,
                                   Integer correctCount,
                                   Integer incorrectCount) {
}
