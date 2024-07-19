package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.constant.AppConstants;
import com.phoenix_sat.phoenix_sat_backend.entity.*;
import com.phoenix_sat.phoenix_sat_backend.entity.metadata.QuestionOptions;
import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import com.phoenix_sat.phoenix_sat_backend.error.exception.AuthenticationException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.mapper.CourseContentResponseMapper;
import com.phoenix_sat.phoenix_sat_backend.mapper.QuestionResponseMapper;
import com.phoenix_sat.phoenix_sat_backend.model.request.*;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import com.phoenix_sat.phoenix_sat_backend.spesification.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseContentRepository courseContentRepository;
    private final CourseContentResponseMapper courseContentResponseMapper;
    private final QuizSectionRepository quizSectionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionResponseMapper questionResponseMapper;
    private final LectureRepository lectureRepository;
    private final QuizRepository quizRepository;
    private final UserInfo userInfo;
    private final AppConstants appConstants;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final ProgressRepository progressRepository;

    @Transactional
    @Override
    public CourseResponse create(CreateCourseRequest courseRequest) {
        User user = userInfo.getUser();
        Organization organization = user.getOrganization();

        Course savedCourse = courseRepository.save(buildCourseByRole(courseRequest, organization));

        CourseAssignment courseAssignment = buildCourseAssignmentByRole(savedCourse, organization);

        courseAssignmentRepository.save(courseAssignment);

        return buildCourseResponse(savedCourse);
    }

    @Override
    public CourseContentResponse getCourseContent(String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course couldn't find by this id: " + courseId));

        List<CourseContent> courseContentList = courseContentRepository.
                getCourseContentsByCourseIdOrderBySequenceNumber(course.getId());

        Progress progress = progressRepository.findByUserId(userInfo.getUser().getId()).orElse(null);
        Integer progressRate = getProgressRate(progress);
        return courseContentResponseMapper.apply(courseContentList, course.getTitle(), progressRate);
    }

    @Override
    public QuizQuestionsResponse getQuizQuestions(String quizId) {
        QuizSection quizSection = quizSectionRepository.findByQuizId(quizId).orElseThrow(() ->
                new ResourceNotFoundException("Quiz not found with this id: " + quizId));

        List<Question> questions = questionRepository.findAllByQuizSectionId(quizSection.getId());
        List<QuestionResponse> questionResponses = questions.stream().map(questionResponseMapper).toList();

        return buildQuizQuestionsResponse(questionResponses);
    }

    @Override
    public QuizCompleteResponse completeQuiz(QuizCompleteRequest quizCompleteRequest) {
        quizRepository.findById(quizCompleteRequest.quizId()).orElseThrow(() ->
                new ResourceNotFoundException("quiz not found with this id: " + quizCompleteRequest.quizId()));

        QuizSection quizSection = quizSectionRepository.findByQuizId(quizCompleteRequest.quizId()).orElseThrow(() ->
                new ResourceNotFoundException("QuizSection not found with this id: " + quizCompleteRequest.quizId()));

        List<Question> questions = questionRepository.findAllByQuizSectionId(quizSection.getId());

        int incorrectCount = getIncorrectCount(quizCompleteRequest, questions);
        return buildQuizCompleteResponse(quizCompleteRequest, questions.size() - incorrectCount, incorrectCount);
    }

    @Override
    public LectureResponse addLecture(CreateLectureRequest createLectureRequest) {
        Course course = courseRepository.findById(createLectureRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createLectureRequest.courseId()));

        if (!course.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId()))
            throw new AuthenticationException("You can't add lecture to another organization's course");

        Integer lastSequence = courseContentRepository.findLastSequenceNumberByCourseId(course.getId());
        Lecture lecture = lectureRepository.save(buildLecture(createLectureRequest));
        courseContentRepository.save(buildCourseContent(course, lecture, lastSequence));

        return LectureResponseBuilder(lecture);
    }

    @Override
    public QuizResponse addQuiz(CreateQuizRequest createQuizRequest) {
        Course course = courseRepository.findById(createQuizRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createQuizRequest.courseId()));

        if (!course.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId()))
            throw new AuthenticationException("You can't add quiz to another organization's course");

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

    @Override
    public Page<CourseResponse> getCoursePage(CourseFilterRequest courseFilterRequest, Pageable pageable) {
        courseFilterRequest.setOrganizationId(userInfo.getUser().getOrganization().getId());

        CourseSpecification courseSpecification = new CourseSpecification(isSuperAdmin(),
                courseFilterRequest,
                appConstants.getDefaultId());

        return courseRepository.findAll(courseSpecification, pageable).map(CourseServiceImpl::buildCourseResponse);
    }

    @Override
    @Transactional
    public CourseResponse confirmCourseAssignment(String courseId) {
        var courseAssignment = courseAssignmentRepository.findByCourseId(courseId).orElseThrow(() ->
                new ResourceNotFoundException("CourseAssignment not found with this courseId: " + courseId));

        courseAssignment.setConfirmed(true);
        courseAssignmentRepository.save(courseAssignment);

        return buildCourseResponse(courseAssignment.getCourse());
    }

    @Override
    @Transactional
    public void delete(String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + courseId));

        if (!userInfo.getUser().getOrganization().getId().equals(course.getOrganization().getId()))
            throw new AuthenticationException("You can't delete another organization's course.");

        course.setIsVisible(false);
        courseRepository.save(course);
    }

    private Integer getProgressRate(Progress progress) {
        if (progress == null)
            return 0;

        int completedParts = progress.getLecturesCompleted() + progress.getQuizzesCompleted();
        Integer allParts = courseContentRepository.findLastSequenceNumberByCourseId(progress.getCourse().getId());

        return (completedParts * 100) / allParts;
    }

    private static int getIncorrectCount(QuizCompleteRequest quizCompleteRequest, List<Question> questions) {
        int incorrectCount = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question questionStep = questions.get(i);
            for (int j = 0; j < questionStep.getCorrectOption().options().size(); j++) {
                if (Objects.equals(questionStep.getCorrectOption().options().get(j),
                        quizCompleteRequest.userAnswers().get(i).answers().get(j)))
                    continue;
                incorrectCount++;
                break;
            }
        }
        return incorrectCount;
    }

    private Boolean isSuperAdmin() {
        for (Role role : userInfo.getUser().getRoles()) {
            if (role.getRole() == RoleType.SUPER_ADMIN)
                return true;
        }
        return false;
    }

    private Boolean isAdmin() {
        for (Role role : userInfo.getUser().getRoles()) {
            if (role.getRole() == RoleType.ADMIN)
                return true;
        }
        return false;
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
                .sequenceNumber(lastSequence == null ? 1 : lastSequence + 1)
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

    private CourseAssignment buildCourseAssignmentByRole(Course savedCourse,
                                                         Organization organization) {
        if (isSuperAdmin())
            return buildCourseAssignment(savedCourse, organization, false);

        return buildCourseAssignment(savedCourse, organization, true);
    }


    private CourseAssignment buildCourseAssignment(Course savedCourse, Organization organization, Boolean confirmed) {
        return CourseAssignment.builder()
                .organization(organization)
                .assignedDate(new Date())
                .course(savedCourse)
                .confirmed(confirmed)
                .build();
    }

    private static CourseResponse buildCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .tags(course.getTags())
                .title(course.getTitle())
                .pictureUrl(course.getPictureUrl())
                .organizationName(course.getOrganization().getName())
                .availablePoint(course.getAvailablePoint())
                .description(course.getDescription())
                .duration(course.getDuration())
                .instructor(course.getInstructor())
                .isVisible(course.getIsVisible())
                .build();
    }

    private Course buildCourseByRole(CreateCourseRequest courseRequest, Organization organization) {
        if (isAdmin()) {
            return getCourse(courseRequest, organization, true);
        }
        if (isSuperAdmin()) {
            return getCourse(courseRequest, organization, false);
        }
        return null;

    }

    private static Course getCourse(CreateCourseRequest courseRequest, Organization organization, Boolean isVisible) {
        return Course.builder()
                .name(courseRequest.name())
                .organization(organization)
                .pictureUrl(courseRequest.pictureUrl())
                .tags(courseRequest.tags())
                .title(courseRequest.title())
                .description(courseRequest.description())
                .instructor(courseRequest.instructor())
                .duration(courseRequest.duration())
                .isDeleted(false)
                .isVisible(isVisible)
                .availablePoint(courseRequest.availablePoint())
                .build();
    }
}
