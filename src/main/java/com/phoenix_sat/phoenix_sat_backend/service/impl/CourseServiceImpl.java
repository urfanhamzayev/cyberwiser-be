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
import com.phoenix_sat.phoenix_sat_backend.error.exception.PermissionDeniedException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.request.*;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import com.phoenix_sat.phoenix_sat_backend.service.CourseService;
import com.phoenix_sat.phoenix_sat_backend.service.loader.CustomMustacheTemplateLoader;
import com.phoenix_sat.phoenix_sat_backend.spesification.CourseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

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
    private final OrganizationRepository organizationRepository;

    @Qualifier(value = "customMustacheTemplateLoader")
    private final CustomMustacheTemplateLoader customMustacheTemplateLoader;

    @Transactional
    @Override
    public CourseResponse create(CreateCourseRequest courseRequest) {
        log.info("Creating a new course with name: {}", courseRequest.name());

        User user = userInfo.getUser();
        Organization organization = user.getOrganization();

        Course savedCourse = courseRepository.save(getCourse(courseRequest, organization, false));

        log.info("Course saved with ID: {}", savedCourse.getId());

        CourseAssignment courseAssignment = buildCourseAssignment(savedCourse, organization, false);

        courseAssignmentRepository.save(courseAssignment);
        log.info("Course assignment saved for course ID: {}", savedCourse.getId());

        return buildCourseResponse(savedCourse);
    }

    @Transactional
    @Override
    public CourseContentResponse getCourseContent(String courseId) {
        log.info("Fetching course content for course ID: {}", courseId);
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course couldn't find by this id: " + courseId));

        if (!(course.getOrganization().getType() == OrganizationType.MAIN)
            && !course.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId())) {
            log.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to get content of another organization's course");
        }

        List<CourseContent> contents = courseContentRepository
                .getCourseContentsByCourseIdAndIsDeletedFalseOrderBySequenceNumber(course.getId());

        List<Progress> progressesOfUser = progressRepository
                .findAllByUserIdAndCourseIdOrderByIsCompletedDescCreateDateAsc(userInfo.getUser().getId(),
                        course.getId());

        for (CourseContent content : contents) {
            boolean isExistProgress = progressesOfUser.stream()
                    .anyMatch(progress -> progress.getContent().getId().equals(content.getId()));

            if (!isExistProgress) {
                Progress progress = buildProgress(userInfo.getUser(), course, content);
                progressRepository.save(progress);
            }
        }

        List<Progress> updatedProgress = progressRepository
                .findAllByUserIdAndCourseIdOrderByIsCompletedDescCreateDateAsc(userInfo.getUser().getId(), course.getId());


        return courseContentResponseConverter.apply(updatedProgress, course.getTitle(), calculateRateOfProgress(progressesOfUser));
    }

    @Override
    public QuizQuestionsResponse getQuizQuestions(String quizId) {
        log.info("Fetching quiz questions for quiz ID: {}", quizId);
        QuizSection quizSection = quizSectionRepository.findByQuizId(quizId).orElseThrow(() ->
                new ResourceNotFoundException("Quiz not found with this id: " + quizId));

        String organizationId = courseContentRepository.findOrganizationIdByQuizIdNativeIsDeletedFalse(quizId).orElseThrow(() ->
                new ResourceNotFoundException("Quiz not found"));

        if (!organizationId.equals(userInfo.getUser().getOrganization().getId())) {
            log.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to get content of another organization's course");
        }

        List<Question> questions = questionRepository.findAllByQuizSectionId(quizSection.getId());
        List<QuestionResponse> questionResponses = questions.stream().map(questionResponseConverter).toList();

        return buildQuizQuestionsResponse(questionResponses);
    }

    @Transactional
    @Override
    public QuizCompleteResponse completeQuiz(QuizCompleteRequest quizCompleteRequest) {
        log.info("Completing quiz with ID: {}", quizCompleteRequest.quizId());

        CourseContent courseContent = courseContentRepository.findByQuizId(quizCompleteRequest.quizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        CourseContent lastCompletedCourseContent = progressRepository
                .getLastCompletedContentByUserIdAndCourseId(userInfo.getUser().getId(), courseContent.getCourse().getId())
                .orElse(null);

        log.info("Checking quiz's order with the sequence number, QuizId:" + quizCompleteRequest.quizId());

        if (lastCompletedCourseContent != null &&
            !lastCompletedCourseContent.getSequenceNumber().equals(courseContent.getSequenceNumber() - 1))
            throw new PermissionDeniedException("Users have to complete parts in order!");

        courseAssignmentRepository.findByCourseIdAndOrganizationIdAndConfirmedTrue(courseContent.getCourse().getId()
                , userInfo.getOrganization().getId()).orElseThrow(()
                -> new ResourceNotFoundException("Course Assignment not found"));


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

        log.info("User: {} - completed quiz successfully with ID: {}", userInfo.getUser().getId(), quizCompleteRequest.quizId());

        return buildQuizCompleteResponse(quizCompleteRequest, questions.size() - incorrectCount, incorrectCount);
    }

    @Transactional
    @Override
    public LectureResponse addLecture(CreateLectureRequest createLectureRequest) {
        log.info("Adding lecture to course ID: {}", createLectureRequest.courseId());
        Course course = courseRepository.findById(createLectureRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createLectureRequest.courseId()));
        CourseAssignment courseAssignment = courseAssignmentRepository.findByCourseIdAndOrganizationIdAndConfirmedTrue(course.getId()
                        , userInfo.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        if (!courseAssignment.getOrganization().getId().equals(userInfo.getOrganization().getId())) {
            log.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to add lecture to another organization's course");
        }

        Integer lastSequence = courseContentRepository.findLastSequenceNumberByCourseIdIsDeletedFalse(course.getId());
        Lecture lecture = lectureRepository.save(buildLecture(createLectureRequest));
        CourseContent courseContent = courseContentRepository.save(buildCourseContent(course, lecture, lastSequence));
        log.info("Lecture added successfully to course ID: {}", createLectureRequest.courseId());

        return lectureResponseBuilder(lecture, false);
    }

    @Transactional
    @Override
    public QuizResponse addQuiz(CreateQuizRequest createQuizRequest) {
        log.info("Adding quiz to course ID: {}", createQuizRequest.courseId());
        Course course = courseRepository.findById(createQuizRequest.courseId()).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + createQuizRequest.courseId()));

        CourseAssignment courseAssignment = courseAssignmentRepository.findByCourseIdAndOrganizationIdAndConfirmedTrue(course.getId()
                        , userInfo.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        if (!courseAssignment.getOrganization().getId().equals(userInfo.getOrganization().getId())) {
            log.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to add lecture to another organization's course");
        }

        Quiz quiz = quizRepository.save(buildQuiz(createQuizRequest));
        Integer lastSequence = courseContentRepository.findLastSequenceNumberByCourseIdIsDeletedFalse(course.getId());
        courseContentRepository.save(buildCourseContent(course, quiz, lastSequence));

        QuizSection quizSection = quizSectionRepository.save(buildQuizSection(createQuizRequest, quiz));
        for (int i = 0; i < createQuizRequest.questionRequestList().size(); i++) {
            Question question = buildQuestion(createQuizRequest, i, quizSection);
            questionRepository.save(question);
        }

        log.info("Quiz added successfully to course ID: {}", createQuizRequest.courseId());

        return buildQuizResponse(createQuizRequest, quiz);
    }

    @Override
    public Page<CourseResponse> getCoursePage(CourseFilterRequest courseFilterRequest, Pageable pageable) {
        log.info("Fetching course page with filters: {}", courseFilterRequest);
        courseFilterRequest.setOrganizationId(userInfo.getUser().getOrganization().getId());

        CourseSpecification courseSpecification = new CourseSpecification(isSuperAdminOrAdmin(),
                courseFilterRequest,
                appConstants.getDefaultId());

        Page<CourseAssignment> courseAssignments = courseAssignmentRepository.
                findAll(courseSpecification, pageable);

        List<CourseResponse> courseResponses = courseAssignments.get().toList().stream().map(courseAssignment -> {
            Completion completion = completionRepository
                    .findByCourseIdAndUserIdAndIsDeletedFalse(courseAssignment.getCourse().getId()
                            , userInfo.getUser().getId()).orElse(null);
            return buildCourseResponsePage(courseAssignment.getCourse(), completion);
        }).toList();

        return new PageImpl<>(courseResponses, pageable, courseResponses.size());

    }

    @Override
    @Transactional
    public CourseResponse confirmCourseAssignment(String assignmentId) {
        log.info("Confirming course assignment for assignment ID: {}", assignmentId);
        var courseAssignment = courseAssignmentRepository.findByIdAndIsDeletedFalse(assignmentId).orElseThrow(() ->
                new ResourceNotFoundException("CourseAssignment not found with this id: " + assignmentId));

        if (!courseAssignment.getOrganization().getId().equals(userInfo.getUser().getOrganization().getId())) {
            log.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not allowed to take this action");
        }
        courseRepository.updateById(courseAssignment.getCourse().getId());
        courseAssignment.setConfirmed(true);
        courseAssignment.getCourse().setIsVisible(true);
        var savedAssignment = courseAssignmentRepository.save(courseAssignment);

        return buildCourseResponse(savedAssignment.getCourse());
    }

    @Override
    @Transactional
    public void delete(String courseId) {
        log.info("Deleting course with ID: {}", courseId);
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course not found with this id: " + courseId));

        if (!userInfo.getUser().getOrganization().getId().equals(course.getOrganization().getId())) {
            log.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not allowed to delete another organization's course.");
        }
        course.setIsVisible(false);

        CourseAssignment courseAssignment = courseAssignmentRepository
                .findByCourseIdAndOrganizationIdAndConfirmedTrue(course.getId(), userInfo.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found"));

        courseAssignment.setIsDeleted(true);

        courseAssignmentRepository.save(courseAssignment);
    }

    @Override
    public LectureResponse completeLecture(String lectureId) {
        log.info("Completing lecture with ID: {}", lectureId);

        CourseContent currentContent = courseContentRepository.findByLectureIdAndIsDeletedFalse(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with the lecture ID:" + lectureId));

        Integer sequenceNum = courseContentRepository.findSequenceNumberByLectureId(lectureId);

        CourseContent lastCompletedContent = progressRepository
                .getLastCompletedContentByUserIdAndCourseId(userInfo.getUser().getId(), currentContent.getCourse().getId())
                .orElse(null);

        if (lastCompletedContent != null && !lastCompletedContent.getSequenceNumber().equals(sequenceNum - 1))
            throw new PermissionDeniedException("Users have to complete parts in order!");

        Progress progress = findProgressByLectureIdAndUserId(lectureId);

        if (!(progress.getCourse().getOrganization().getType() == OrganizationType.MAIN))
            validateOrganizationAccess(progress.getCourse().getOrganization().getId());

        markProgressAsCompleted(progress);
        if (checkAndHandleCourseCompletion(progress)) {
            var completion = buildCompletion(progress);
            completionRepository.save(completion);
        }

        log.info("User: {} - completed lecture with ID: {}", userInfo.getUser().getId(), lectureId);

        return lectureResponseBuilder(progress.getContent().getLecture(), progress.getIsCompleted());
    }

    @Override
    public List<CourseAssignResponse> assign(CourseAssignRequest courseAssignRequest) {
        Course course = courseRepository.findById(courseAssignRequest.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found. Course id:" + courseAssignRequest.courseId()));

        List<Organization> organizations = courseAssignRequest.organizationsId().stream().map(organizationId ->
                organizationRepository.findById(organizationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Organization not found. ID:" + organizationId))).toList();

        List<CourseAssignment> courseAssignment = courseAssignmentRepository.saveAll(organizations.stream().map(organization ->
                buildCourseAssignment(course, organization, false)).toList());

        return courseAssignment.stream().map(courseAssignment1 ->
                buildCourseAssignResponse(courseAssignment1, course)).toList();
    }

    @Override
    public List<CourseAssignResponse> getAssignments() {
        List<CourseAssignment> courseAssignments = courseAssignmentRepository
                .findAllByOrganizationIdAndIsDeletedFalse(userInfo.getOrganization().getId());

        return courseAssignments
                .stream()
                .map(courseAssignment -> buildCourseAssignResponse(courseAssignment, courseAssignment.getCourse()))
                .toList();
    }

    @Override
    public CourseResponse update(CourseUpdateRequest courseUpdateRequest) {
        log.info("Course update process starting... -->CourseId:" + courseUpdateRequest.courseId());
        Course existingCourse = courseRepository.findById(courseUpdateRequest.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found, CourseId:" + courseUpdateRequest.courseId()));

        validateOrganizationAccess(existingCourse.getOrganization().getId());

        checkNotEmptyAndNotNullAndSetFieldExistingCourse(courseUpdateRequest, existingCourse);

        courseRepository.save(existingCourse);

        log.info("Course {} updated successfully", existingCourse.getId());
        return buildCourseResponse(existingCourse);
    }

    @Override
    public byte[] generateCompletionReport(String courseId) {
        Course course = courseRepository.findByIdAndIsDeletedFalseAndIsVisibleTrue(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course is not exist with id:" + courseId));

        courseAssignmentRepository.findByCourseIdAndOrganizationIdAndConfirmedTrue(course.getId(), userInfo.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        Completion completion = completionRepository.findByCourseIdAndUserIdAndIsDeletedFalse(course.getId(), userInfo.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Certificate can't be generated without completion."));

        return generateCertificate(userInfo.getUser(), course,completion.getCompletionDate());
    }

    public byte[] generateCertificate(User user, Course course, Date completionDate) {
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", user.getFullName());
        data.put("courseName", course.getName());
        data.put("completionDate", getFormattedDate(completionDate));

        String htmlContent = customMustacheTemplateLoader.loadTemplate("certificate.mustache", data);

        return convertHtmlToPdf(htmlContent);
    }

    private static String getFormattedDate(Date completionDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(completionDate);
    }

    private byte[] convertHtmlToPdf(String htmlContent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ITextRenderer renderer = new ITextRenderer();

            // Set the base URL to resolve relative paths (if you have images or other resources)
            String baseUrl = CourseServiceImpl.class.getResource("/templates/").toString();
            renderer.setDocumentFromString(htmlContent, baseUrl);

            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return outputStream.toByteArray();
    }

    private static void checkNotEmptyAndNotNullAndSetFieldExistingCourse(CourseUpdateRequest courseUpdateRequest, Course existingCourse) {
        if (courseUpdateRequest.name() != null && !courseUpdateRequest.name().isEmpty()) {
            existingCourse.setName(courseUpdateRequest.name());
        }
        if (courseUpdateRequest.pictureUrl() != null && !courseUpdateRequest.pictureUrl().isEmpty()) {
            existingCourse.setPictureUrl(courseUpdateRequest.pictureUrl());
        }
        if (courseUpdateRequest.description() != null && !courseUpdateRequest.description().isEmpty()) {
            existingCourse.setDescription(courseUpdateRequest.description());
        }
        if (courseUpdateRequest.tags() != null && !courseUpdateRequest.tags().isEmpty()) {
            existingCourse.setTags(courseUpdateRequest.tags());
        }
        if (courseUpdateRequest.availablePoint() != null) {
            existingCourse.setAvailablePoint(courseUpdateRequest.availablePoint());
        }
        if (courseUpdateRequest.duration() != null && !courseUpdateRequest.duration().isEmpty()) {
            existingCourse.setDuration(courseUpdateRequest.duration());
        }
        if (courseUpdateRequest.instructor() != null && !courseUpdateRequest.instructor().isEmpty()) {
            existingCourse.setInstructor(courseUpdateRequest.instructor());
        }
        if (courseUpdateRequest.title() != null && !courseUpdateRequest.title().isEmpty()) {
            existingCourse.setTitle(courseUpdateRequest.title());
        }
    }

    private static CourseAssignResponse buildCourseAssignResponse(CourseAssignment courseAssignment1, Course course) {
        return CourseAssignResponse.builder()
                .assignmentId(courseAssignment1.getId())
                .courseId(course.getId())
                .courseName(course.getName())
                .organizationName(courseAssignment1.getOrganization().getName())
                .organizationId(courseAssignment1.getOrganization().getId())
                .assignedDate(new Date())
                .confirmed(courseAssignment1.getConfirmed())
                .build();
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
        return progressRepository.findByLectureIdAndUserIdAndIsDeletedFalse(lectureId, userInfo.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with this id: " + lectureId));
    }

    private Progress findProgressByQuizIdAndUserId(String quizId) {
        return progressRepository.findByQuizIdAndUserIdAndIsDeletedFalse(quizId, userInfo.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found with this quizId: " + quizId));
    }

    private void validateOrganizationAccess(String contentOrganizationId) {
        if (!userInfo.getUser().getOrganization().getId().equals(contentOrganizationId)) {
            log.warn("Unauthorized access detected userId: {}", userInfo.getUser().getId());
            throw new AuthenticationException("You are not assigned to access another organization's course.");
        }
    }

    private void markProgressAsCompleted(Progress progress) {
        progress.setIsCompleted(true);
        progress.setUpdateDate(new Date());
        progressRepository.save(progress);
    }

    private boolean checkAndHandleCourseCompletion(Progress progress) {
        List<Boolean> isCompletedList = progressRepository.findIsCompletedByCourseIdAndUserId(
                progress.getCourse().getId(), userInfo.getUser().getId());
        return checkIsCompletedAllContents(isCompletedList);
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

    private static Progress buildProgress(User user, Course course, CourseContent courseContent) {
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

    private Boolean isSuperAdminOrAdmin() {
        for (Role role : userInfo.getUser().getRoles()) {
            if ((role.getRole() == RoleType.SUPER_ADMIN) || (role.getRole() == RoleType.ADMIN))
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
                .availablePoint(course.getAvailablePoint())
                .description(course.getDescription())
                .duration(course.getDuration())
                .instructor(course.getInstructor())
                .isVisible(course.getIsVisible())
                .build();
    }

    private static CourseResponse buildCourseResponsePage(Course course, Completion completion) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .tags(course.getTags())
                .title(course.getTitle())
                .pictureUrl(course.getPictureUrl())
                .availablePoint(course.getAvailablePoint())
                .description(course.getDescription())
                .duration(course.getDuration())
                .instructor(course.getInstructor())
                .isVisible(course.getIsVisible())
                .isCompleted(completion != null)
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
                .isVisible(isVisible)
                .availablePoint(courseRequest.availablePoint())
                .build();
    }
}
