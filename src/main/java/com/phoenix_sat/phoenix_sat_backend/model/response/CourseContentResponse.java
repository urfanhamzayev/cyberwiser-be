package com.phoenix_sat.phoenix_sat_backend.model.response;

import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import lombok.Builder;

import java.util.List;
@Builder
public record CourseContentResponse(String title,
                                    Integer progressRate,
                                    List<Object> lecturesAndQuizzes
                                    ) {

}
