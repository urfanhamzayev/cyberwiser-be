package com.phoenix_sat.phoenix_sat_backend.mapper;

import com.phoenix_sat.phoenix_sat_backend.entity.Question;
import com.phoenix_sat.phoenix_sat_backend.model.response.QuestionResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;
@Component
public class QuestionResponseMapper implements Function<Question, QuestionResponse> {
    @Override
    public QuestionResponse apply(Question question) {
        return QuestionResponse.builder()
                .questionId(question.getId())
                .questionText(question.getText())
                .questionOptions(question.getQuestionOptions().options())
                .selectionType(question.getSelectionType())
                .build();
    }
}
