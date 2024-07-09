package com.phoenix_sat.phoenix_sat_backend.mapper;

import com.phoenix_sat.phoenix_sat_backend.entity.CourseContent;
import com.phoenix_sat.phoenix_sat_backend.model.response.CourseContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseContentResponseMapper {
    private final QuizResponseMapper quizResponseMapper;
    private final LectureResponseMapper lectureResponseMapper;

    public CourseContentResponse apply(List<CourseContent> courseContent, String title, Integer progressRate) {
        List<Object> lecturesAndQuizzes = new ArrayList<>();
        for (int i = 0; i < courseContent.size(); i++) {
            var content = courseContent.get(i);
            lecturesAndQuizzes.add(i, content.getLecture() == null ?
                    quizResponseMapper.apply(content.getQuiz()) : lectureResponseMapper.apply(content.getLecture()));
        }

        return CourseContentResponse.builder().title(title).lecturesAndQuizzes(lecturesAndQuizzes).progressRate(progressRate).build();
    }
}
