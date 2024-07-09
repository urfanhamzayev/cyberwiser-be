package com.phoenix_sat.phoenix_sat_backend.model.response;

import com.phoenix_sat.phoenix_sat_backend.enums.SelectionType;
import lombok.Builder;

import java.util.List;
@Builder
public record QuestionResponse(String questionId,
                               String questionText,
                               List<String> questionOptions,
                               SelectionType selectionType) {
}
