package com.phoenix_sat.phoenix_sat_backend.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix_sat.phoenix_sat_backend.entity.metadata.QuestionOptions;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
@Converter
@RequiredArgsConstructor
public class QuestionOptionsConverter implements AttributeConverter<QuestionOptions, String> {
    private final ObjectMapper objectMapper;
    @Override
    public String convertToDatabaseColumn(QuestionOptions attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error while convert QuestionOptions to JSON",e);
        }
    }

    @Override
    public QuestionOptions convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData,QuestionOptions.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error while convert JSON to QuestionOptions",e);
        }
    }
}
