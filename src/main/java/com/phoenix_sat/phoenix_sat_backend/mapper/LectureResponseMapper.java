package com.phoenix_sat.phoenix_sat_backend.mapper;

import com.phoenix_sat.phoenix_sat_backend.entity.Lecture;
import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import com.phoenix_sat.phoenix_sat_backend.model.response.LectureResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LectureResponseMapper implements Function<Lecture, LectureResponse> {
    @Override
    public LectureResponse apply(Lecture lecture) {
        return LectureResponse.builder()
                .lectureId(lecture.getId())
                .duration(lecture.getDuration())
                .title(lecture.getTitle())
                .videoUrl(lecture.getVideoUrl())
                .contentType(ContentType.LECTURE)
                .isCompleted(false)
                .build();
    }
}
