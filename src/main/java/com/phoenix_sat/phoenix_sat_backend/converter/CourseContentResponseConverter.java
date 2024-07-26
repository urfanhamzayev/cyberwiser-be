package com.phoenix_sat.phoenix_sat_backend.converter;

import com.phoenix_sat.phoenix_sat_backend.entity.CourseContent;
import com.phoenix_sat.phoenix_sat_backend.entity.Progress;
import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import com.phoenix_sat.phoenix_sat_backend.model.response.CourseContentResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.LectureResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.QuizResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseContentResponseConverter {

    public CourseContentResponse apply(List<Progress> progresses, String title, Integer progressRate) {
        List<Object> lecturesAndQuizzes = new ArrayList<>();
        List<CourseContent> courseContents = progresses.stream().map(Progress::getContent).toList();
        for (int i = 0; i < courseContents.size(); i++) {
            var content = courseContents.get(i);
            lecturesAndQuizzes.add(i, content.getLecture() == null ? getQuizResponse(content, progresses.get(i))
                     : getLectureResponse(content,progresses.get(i)));
        }

        return CourseContentResponse.builder()
                .title(title)
                .lecturesAndQuizzes(lecturesAndQuizzes)
                .progressRate(progressRate)
                .build();
    }

    private QuizResponse getQuizResponse(CourseContent content,Progress progress) {
        return QuizResponse.builder()
                .title(content.getQuiz().getTitle())
                .quizId(content.getQuiz().getId())
                .numberOfQuestions(content.getQuiz().getNumberOfQuestions())
                .contentType(ContentType.QUIZ)
                .isCompleted(progress.getIsCompleted())
                .build();
    }
    private LectureResponse getLectureResponse(CourseContent content, Progress progress) {
            return LectureResponse.builder()
                    .lectureId(content.getLecture().getId())
                    .duration(content.getLecture().getDuration())
                    .title(content.getLecture().getTitle())
                    .videoUrl(content.getLecture().getVideoUrl())
                    .contentType(ContentType.LECTURE)
                    .isCompleted(progress.getIsCompleted())
                    .build();

    }


}
