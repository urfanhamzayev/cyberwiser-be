package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.phoenix_sat.phoenix_sat_backend.entity.metadata.QuestionOptions;
import com.phoenix_sat.phoenix_sat_backend.enums.SelectionType;
import jakarta.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public record QuestionRequest(@NotEmpty String text,
                              List<String> questionOptions,
                              List<String> correctOptions,
                              @NotNull SelectionType selectionType) {
}
