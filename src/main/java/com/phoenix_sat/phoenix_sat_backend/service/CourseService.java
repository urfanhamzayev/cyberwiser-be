package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.entity.*;
import com.phoenix_sat.phoenix_sat_backend.entity.converter.QuestionOptionsConverter;
import com.phoenix_sat.phoenix_sat_backend.entity.metadata.QuestionOptions;
import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import com.phoenix_sat.phoenix_sat_backend.enums.SelectionType;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.mapper.CourseContentResponseMapper;
import com.phoenix_sat.phoenix_sat_backend.mapper.QuestionResponseMapper;
import com.phoenix_sat.phoenix_sat_backend.model.request.CreateCourseRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.CreateLectureRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.CreateQuizRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.QuizCompleteRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import com.phoenix_sat.phoenix_sat_backend.repository.projection.CorrectOptionProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseContentRepository courseContentRepository;
    private final CourseContentResponseMapper courseContentResponseMapper;
    private final QuizSectionRepository quizSectionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionResponseMapper questionResponseMapper;
    private final LectureRepository lectureRepository;
    private final QuizRepository quizRepository;

    public CourseContentResponse getCourseContent(String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course couldn't find by this id: " + courseId));

        List<CourseContent> courseContentList = courseContentRepository.getCourseContentsByCourseIdOrderBySequenceNumber(course.getId());

        return courseContentResponseMapper.apply(courseContentList, "Spring sec", 80);
    }

    public QuizQuestionsResponse getQuizQuestions(String quizId) {
        QuizSection quizSection = quizSectionRepository.findByQuizId(quizId).orElseThrow(() ->
                new ResourceNotFoundException("Quiz not found with this id: " + quizId));

        List<Question> questions = questionRepository.findAllByQuizSectionId(quizSection.getId());
        List<QuestionResponse> questionResponses = questions.stream().map(questionResponseMapper).toList();

        return buildQuizQuestionsResponse(questionResponses);
    }

    public QuizCompleteResponse completeQuiz(QuizCompleteRequest quizCompleteRequest) {
        quizRepository.findById(quizCompleteRequest.quizId()).orElseThrow(()->
                new ResourceNotFoundException("quiz not found with this id: " + quizCompleteRequest.quizId()));

        QuizSection quizSection = quizSectionRepository.findByQuizId(quizCompleteRequest.quizId()).orElseThrow(() ->
                new ResourceNotFoundException("QuizSection not found with this id: " + quizCompleteRequest.quizId()));

        List<Question> questions = questionRepository.findAllByQuizSectionId(quizSection.getId());

        int incorrectCount = getIncorrectCount(quizCompleteRequest, questions);
        return buildQuizCompleteResponse(quizCompleteRequest, questions.size()-incorrectCount, incorrectCount);
    }

    private static int getIncorrectCount(QuizCompleteRequest quizCompleteRequest, List<Question> questions) {
        int incorrectCount = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question questionStep = questions.get(i);
            for (int j = 0; j < questionStep.getCorrectOption().correctOptions().size(); j++) {
                if(Objects.equals(questionStep.getCorrectOption().correctOptions().get(j),
                        quizCompleteRequest.userAnswers().get(i).answers().get(j)))
                    continue;
                incorrectCount++;
                break;
            }
        }
        return incorrectCount;
    }

    public LectureResponse addLecture(CreateLectureRequest createLectureRequest) {
        Course course = courseRepository.findById(createLectureRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createLectureRequest.courseId()));
        Integer lastSequence = courseContentRepository.findLastSequenceNumberByCourseId(course.getId());

        Lecture lecture = lectureRepository.save(buildLecture(createLectureRequest));

        courseContentRepository.save(buildCourseContent(course, lecture, lastSequence));

        return LectureResponseBuilder(lecture);
    }


    public QuizResponse addQuiz(CreateQuizRequest createQuizRequest) {
        Course course = courseRepository.findById(createQuizRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createQuizRequest.courseId()));

        Quiz quiz = quizRepository.save(buildQuiz(createQuizRequest));

        Integer lastSequence = courseContentRepository.findLastSequenceNumberByCourseId(course.getId());

        courseContentRepository.save(buildCourseContent(course, quiz, lastSequence));

        QuizSection quizSection = quizSectionRepository.save(buildQuizSection(createQuizRequest, quiz));
        for (int i = 0; i < createQuizRequest.questionRequestList().size(); i++) {

            Question question = buildQuestion(createQuizRequest, i, quizSection);
            questionRepository.save(question);
        }
        return buildQuizResponse(createQuizRequest, quiz);
    }

    private static QuizResponse buildQuizResponse(CreateQuizRequest createQuizRequest, Quiz quiz) {
        return QuizResponse.builder()
                .quizId(quiz.getId())
                .isCompleted(false)
                .contentType(ContentType.QUIZ)
                .numberOfQuestions(createQuizRequest.numberOfQuestions())
                .title(createQuizRequest.quizTitle())
                .build();
    }

    private static QuizCompleteResponse buildQuizCompleteResponse(QuizCompleteRequest quizCompleteRequest,
                                                                  int correctCount,
                                                                  int incorrectCount) {
        return QuizCompleteResponse.builder().rate((correctCount * 100 / (incorrectCount + correctCount)))
                .incorrectCount(incorrectCount)
                .correctCount(correctCount)
                .quizId(quizCompleteRequest.quizId())
                .build();
    }

    private static Question buildQuestion(CreateQuizRequest createQuizRequest, int i, QuizSection quizSection) {
        return Question.builder()
                .questionOptions(new QuestionOptions(createQuizRequest.questionRequestList().get(i).questionOptions()))
                .correctOption(new QuestionOptions(createQuizRequest.questionRequestList().get(i).correctOptions()))
                .selectionType(createQuizRequest.questionRequestList().get(i).selectionType())
                .text(createQuizRequest.questionRequestList().get(i).text())
                .quizSection(quizSection)
                .build();
    }

    private static QuizSection buildQuizSection(CreateQuizRequest createQuizRequest, Quiz quiz) {
        return QuizSection.builder()
                .quiz(quiz)
                .title(createQuizRequest.quizSectionTitle())
                .build();
    }

    private static QuizQuestionsResponse buildQuizQuestionsResponse(List<QuestionResponse> questionResponses) {
        return QuizQuestionsResponse.builder()
                .questionResponses(questionResponses)
                .build();
    }

    private static LectureResponse LectureResponseBuilder(Lecture lecture) {
        return LectureResponse.builder()
                .lectureId(lecture.getId())
                .contentType(ContentType.LECTURE)
                .isCompleted(false)
                .videoUrl(lecture.getVideoUrl())
                .title(lecture.getTitle())
                .build();
    }

    private static CourseContent buildCourseContent(Course course, Object content, Integer lastSequence) {
        var courseContent = CourseContent.builder()
                .course(course)
                .type(ContentType.QUIZ)
                .sequenceNumber(lastSequence + 1)
                .build();
        if (content instanceof Lecture)
            courseContent.setLecture((Lecture) content);
        else if (content instanceof Quiz)
            courseContent.setQuiz((Quiz) content);
        return courseContent;
    }

    private static Lecture buildLecture(CreateLectureRequest createLectureRequest) {
        return Lecture.builder()
                .videoUrl(createLectureRequest.videoUrl())
                .title(createLectureRequest.title())
                .duration(createLectureRequest.duration())
                .build();
    }

    private static Quiz buildQuiz(CreateQuizRequest createQuizRequest) {
        return Quiz.builder()
                .numberOfQuestions(createQuizRequest.numberOfQuestions())
                .title(createQuizRequest.quizTitle())
                .build();
    }

    public CreateCourseResponse create(CreateCourseRequest courseRequest) {
        return null;
    }
}
