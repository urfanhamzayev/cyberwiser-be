package com.phoenix_sat.phoenix_sat_backend.repository.projection;

import com.phoenix_sat.phoenix_sat_backend.entity.metadata.QuestionOptions;

public interface CorrectOptionProjection {
    QuestionOptions getCorrectOption();
}
