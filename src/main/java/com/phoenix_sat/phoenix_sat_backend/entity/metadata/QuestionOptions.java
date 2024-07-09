package com.phoenix_sat.phoenix_sat_backend.entity.metadata;

import java.io.Serializable;
import java.util.List;

public record QuestionOptions(List<String> correctOptions) implements Serializable {
}
