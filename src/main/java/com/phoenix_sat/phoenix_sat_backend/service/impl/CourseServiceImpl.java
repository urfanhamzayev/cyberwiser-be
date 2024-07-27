package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.constant.AppConstants;
import com.phoenix_sat.phoenix_sat_backend.converter.CourseContentResponseConverter;
import com.phoenix_sat.phoenix_sat_backend.converter.QuestionResponseConverter;
import com.phoenix_sat.phoenix_sat_backend.entity.*;
import com.phoenix_sat.phoenix_sat_backend.entity.metadata.QuestionOptions;
import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import com.phoenix_sat.phoenix_sat_backend.error.exception.AuthenticationException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.InvalidAnswerException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.request.*;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import com.phoenix_sat.phoenix_sat_backend.spesification.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final CourseContentRepository courseContentRepository;
    private final CourseContentResponseConverter courseContentResponseConverter;
    private final QuizSectionRepository quizSectionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionResponseConverter questionResponseConverter;
    private final LectureRepository lectureRepository;
    private final QuizRepository quizRepository;
    private final UserInfo userInfo;
    private final AppConstants appConstants;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final CompletionRepository completionRepository;

    @Transactional
    @Override
    public CourseResponse create(CreateCourseRequest courseRequest) {
        logger.info("Creating a new course with name: {}", courseRequest.name());
        User user = userInfo.getUser();
        Organization organization = user.getOrganization();

        Course savedCourse = courseRepository.save(Objects.requireNonNull(buildCourseByRole(courseRequest, organization)));
        logger.info("Course saved with ID: {}", savedCourse.getId());

        CourseAssignment courseAssignment = buildCourseAssignmentByRole(savedCourse, organization);
        courseAssignmentRepository.save(courseAssignment);
        logger.info("Course assignment saved for course ID: {}", savedCourse.getId());

        return buildCourseResponse(savedCourse);
    }

    @Transactional
    @Override
    public CourseContentResponse getCourseContent(String courseId) {
        logger.info("Fetching course content for course ID: {}", courseId);
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course couldn't find by this id: " + courseId));

        if (!(course.getOrganization().getType() == OrganizationType.MAIN)
            && !course.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId())) {
            logger.warn("Unauthorized access detected userId: {}",userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to get content of another organization's course");
        }
        List<Progress> progressesOfUser = progressRepository.findAllByUserIdAndCourseIdOrderByCreateDate(userInfo.getUser().getId(),
                course.getId());

        return courseContentResponseConverter.apply(progressesOfUser, course.getTitle(), calculateRateOfProgress(progressesOfUser));
    }

    @Override
    public QuizQuestionsResponse getQuizQuestions(String quizId) {
        logger.info("Fetching quiz questions for quiz ID: {}", quizId);
        QuizSection quizSection = quizSectionRepository.findByQuizId(quizId).orElseThrow(() ->
                new ResourceNotFoundException("Quiz not found with this id: " + quizId));

        String organizationId = courseContentRepository.findOrganizationIdByQuizIdNative(quizId).orElseThrow(() ->
                new ResourceNotFoundException("Quiz not found"));

        if (!organizationId.equals(userInfo.getUser().getOrganization().getId())) {
            logger.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to get content of another organization's course");
        }

        List<Question> questions = questionRepository.findAllByQuizSectionId(quizSection.getId());
        List<QuestionResponse> questionResponses = questions.stream().map(questionResponseConverter).toList();

        return buildQuizQuestionsResponse(questionResponses);
    }

    @Transactional
    @Override
    public QuizCompleteResponse completeQuiz(QuizCompleteRequest quizCompleteRequest) {
        logger.info("Completing quiz with ID: {}", quizCompleteRequest.quizId());

        String organizationId = findOrganizationIdByQuizId(quizCompleteRequest.quizId());
        if (!organizationId.equals(appConstants.getDefaultId()))
            validateOrganizationAccess(organizationId);

        quizRepository.findById(quizCompleteRequest.quizId()).orElseThrow(() ->
                new ResourceNotFoundException("Quiz not found with this id: " + quizCompleteRequest.quizId()));

        List<Question> questions = findQuestionsByQuizId(quizCompleteRequest.quizId());
        if (questions.size() != quizCompleteRequest.userAnswers().size())
            throw new InvalidAnswerException("The number of answers provided does not match the number of questions.");

        int incorrectCount = getIncorrectCount(quizCompleteRequest, questions);
        Progress progress = findProgressByQuizIdAndUserId(quizCompleteRequest.quizId());
        markProgressAsCompleted(progress);

        if (checkAndHandleCourseCompletion(progress)) {
            var completion = buildCompletion(progress);
            completionRepository.save(completion);
        }

        logger.info("User: {} - completed quiz successfully with ID: {}", userInfo.getUser().getId(), quizCompleteRequest.quizId());

        return buildQuizCompleteResponse(quizCompleteRequest, questions.size() - incorrectCount, incorrectCount);
    }

    @Transactional
    @Override
    public LectureResponse addLecture(CreateLectureRequest createLectureRequest) {
        logger.info("Adding lecture to course ID: {}", createLectureRequest.courseId());
        Course course = courseRepository.findById(createLectureRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createLectureRequest.courseId()));

        if (!course.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId())) {
            logger.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to add lecture to another organization's course");
        }

        Integer lastSequence = courseContentRepository.findLastSequenceNumberByCourseId(course.getId());
        Lecture lecture = lectureRepository.save(buildLecture(createLectureRequest));
        CourseContent courseContent = courseContentRepository.save(buildCourseContent(course, lecture, lastSequence));
        setProgressForUsers(course, courseContent);
        logger.info("Lecture added successfully to course ID: {}", createLectureRequest.courseId());

        return lectureResponseBuilder(lecture, false);
    }

    @Transactional
    @Override
    public QuizResponse addQuiz(CreateQuizRequest createQuizRequest) {
        logger.info("Adding quiz to course ID: {}", createQuizRequest.courseId());
        Course course = courseRepository.findById(createQuizRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createQuizRequest.courseId()));

        if (!course.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId())) {
            logger.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to add quiz to another organization's course");
        }

        Quiz quiz = quizRepository.save(buildQuiz(createQuizRequest));
        Integer lastSequence = courseContentRepository.findLastSequenceNumberByCourseId(course.getId());
        CourseContent courseContent = courseContentRepository.save(buildCourseContent(course, quiz, lastSequence));

        QuizSection quizSection = quizSectionRepository.save(buildQuizSection(createQuizRequest, quiz));
        for (int i = 0; i < createQuizRequest.questionRequestList().size(); i++) {
            Question question = buildQuestion(createQuizRequest, i, quizSection);
            questionRepository.save(question);
        }

        setProgressForUsers(course, courseContent);
        logger.info("Quiz added successfully to course ID: {}", createQuizRequest.courseId());

        return buildQuizResponse(createQuizRequest, quiz);
    }

    @Override
    public Page<CourseResponse> getCoursePage(CourseFilterRequest courseFilterRequest, Pageable pageable) {
        logger.info("Fetching course page with filters: {}", courseFilterRequest);
        courseFilterRequest.setOrganizationId(userInfo.getUser().getOrganization().getId());

        CourseSpecification courseSpecification = new CourseSpecification(isSuperAdmin(),
                courseFilterRequest,
                appConstants.getDefaultId());

        return courseRepository.findAll(courseSpecification, pageable).map(CourseServiceImpl::buildCourseResponse);
    }

    @Override
    @Transactional
    public CourseResponse confirmCourseAssignment(String courseId) {
        logger.info("Confirming course assignment for course ID: {}", courseId);
        var courseAssignment = courseAssignmentRepository.findByCourseId(courseId).orElseThrow(() ->
                new ResourceNotFoundException("CourseAssignment not found with this courseId: " + courseId));

        if (!courseAssignment.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId())) {
            logger.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not allowed to take this action");
        }
        courseRepository.updateById(courseId);
        courseAssignment.setConfirmed(true);
        var savedAssignment = courseAssignmentRepository.save(courseAssignment);

        return buildCourseResponse(savedAssignment.getCourse());
    }

    @Override
    @Transactional
    public void delete(String courseId) {
        logger.info("Deleting course with ID: {}", courseId);
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + courseId));

        if (!userInfo.getUser().getOrganization().getId().equals(course.getOrganization().getId())) {
            logger.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not allowed to delete another organization's course.");
        }
        course.setIsVisible(false);
        courseRepository.save(course);
    }

    @Override
    public LectureResponse completeLecture(String lectureId) {
        logger.info("Completing lecture with ID: {}", lectureId);
        Progress progress = findProgressByLectureIdAndUserId(lectureId);

        if (!(progress.getCourse().getOrganization().getType() == OrganizationType.MAIN))
            validateOrganizationAccess(progress.getCourse().getOrganization().getId());

        markProgressAsCompleted(progress);
        if (checkAndHandleCourseCompletion(progress)) {
            var completion = buildCompletion(progress);
            completionRepository.save(completion);
        }
        logger.info("User: {} - completed lecture with ID: {}", userInfo.getUser().getId(), lectureId);

        return lectureResponseBuilder(progress.getContent().getLecture(), true);
    }

    private Integer calculateRateOfProgress(List<Progress> progresses) {
        int completedCount = 0;
        int totalContentCount = 0;
        for (var progress : progresses) {
            if (progress.getIsCompleted()) {
                completedCount++;
                totalContentCount++;
                continue;
            }
            totalContentCount++;
        }

        return totalContentCount == 0 ? 0 : (completedCount * 100) / totalContentCount;
    }

    private Progress findProgressByLectureIdAndUserId(String lectureId) {
        return progressRepository.findByLectureIdAndUserId(lectureId, userInfo.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with this id: " + lectureId));
    }

    private Progress findProgressByQuizIdAndUserId(String quizId) {
        return progressRepository.findByQuizIdAndUserId(quizId, userInfo.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found with this quizId: " + quizId));
    }

    private void validateOrganizationAccess(String contentOrganizationId) {
        if (!userInfo.getUser().getOrganization().getId().equals(contentOrganizationId)) {
            logger.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to complete another organization's content.");
        }
    }

    private void markProgressAsCompleted(Progress progress) {
        progress.setIsCompleted(true);
        progressRepository.save(progress);
    }

    private boolean checkAndHandleCourseCompletion(Progress progress) {
        List<Boolean> isCompletedList = progressRepository.findIsCompletedByCourseIdAndUserId(
                progress.getCourse().getId(), userInfo.getUser().getId());
        return checkIsCompletedAllContents(isCompletedList);
    }

    private String findOrganizationIdByQuizId(String quizId) {
        return courseContentRepository.findOrganizationIdByQuizIdNative(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }

    private List<Question> findQuestionsByQuizId(String quizId) {
        QuizSection quizSection = quizSectionRepository.findByQuizId(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSection not found with this id: " + quizId));
        return questionRepository.findAllByQuizSectionId(quizSection.getId());
    }

    private Completion buildCompletion(Progress progress) {
        return Completion.builder()
                .user(userInfo.getUser())
                .course(progress.getCourse())
                .completionDate(new Date())
                .build();
    }

    private static Progress buildProgressWhenContentCreated(User user, Course course, CourseContent courseContent) {
        return Progress.builder()
                .user(user)
                .course(course)
                .content(courseContent)
                .isCompleted(false)
                .build();
    }

    private boolean checkIsCompletedAllContents(List<Boolean> isCompletedList) {
        for (boolean b : isCompletedList) {
            if (!b)
                return false;
        }
        return true;
    }

    private void setProgressForUsers(Course course, CourseContent courseContent) {
        List<User> users;

        if (isSuperAdmin())
            users = userRepository.findAll();
        else
            users = userRepository.findUsersByOrganizationIdAndIsActiveTrue(userInfo.getUser().getOrganization().getId());

        List<Progress> progresses = users.stream().map(user -> buildProgressWhenContentCreated(user, course, courseContent)).toList();

        progressRepository.saveAll(progresses);
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

    private static LectureResponse lectureResponseBuilder(Lecture lecture, Boolean isCompleted) {
        return LectureResponse.builder()
                .lectureId(lecture.getId())
                .contentType(ContentType.LECTURE)
                .isCompleted(isCompleted)
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
