package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.entity.*;
import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import com.phoenix_sat.phoenix_sat_backend.error.exception.AuthenticationException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceAlreadyExistException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.jwt.JwtToken;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserLoginRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserRegisterRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import com.phoenix_sat.phoenix_sat_backend.security.JWTProvider;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final UserInfo userInfo;
    private final ProgressRepository progressRepository;
    private final CompletionRepository completionRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;

    @Override
    public JwtToken logIn(UserLoginRequest userLoginRequest) {
        log.info("User login process starting...");
        User user = userRepository.findByEmailAndIsActiveTrue(userLoginRequest.email()).orElseThrow(() ->
                new ResourceNotFoundException("User not found with this email: " + userLoginRequest.email()));
        boolean isMatch = passwordEncoder.matches(userLoginRequest.password(), user.getPassword());
        if (isMatch) {
            log.info("User logged successfully. UserId: {}", user.getId());
            return buildJwtToken(user);
        }
        throw new AuthenticationException("Bad credentials");
    }

    @Override
    public UserProfileResponse getUserProfile() {
        return buildUserProfileResponse();
    }


    @Override
    public UserProgressReport getUserProgress() {
        return getUserProgressByUserId(userInfo.getUser().getId());
    }

    @Override
    public List<UserProgressReport> getAllUserProgress() {
        List<User> users = userRepository.findUsersByOrganizationIdAndIsActiveTrue(userInfo.getOrganization().getId());

        return users.stream().map(user -> getUserProgressByUserId(user.getId())).toList();
    }

    @Override
    public UserRegisterResponse register(String organizationId, UserRegisterRequest userRegisterRequest) {
        Boolean isExist = userRepository.existsByEmailAndIsActiveTrueAndIsDeletedFalse(userRegisterRequest.email());

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() ->
                new ResourceNotFoundException("Organization not found. Id:" + organizationId));
        if (isExist)
            throw new ResourceAlreadyExistException("User already exist with this email :" + userRegisterRequest.email());

        User user = buildUserFromRegisterRequest(userRegisterRequest, organization);

        var savedUser = userRepository.save(user);

        return UserRegisterResponse.builder()
                .isActive(savedUser.getIsActive())
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .build();

    }

    private User buildUserFromRegisterRequest(UserRegisterRequest userRegisterRequest, Organization organization) {
        return User.builder()
                .firstName(userRegisterRequest.firstName())
                .lastName(userRegisterRequest.lastName())
                .email(userRegisterRequest.email())
                .password(passwordEncoder.encode(userRegisterRequest.password()))
                .pictureUrl(userRegisterRequest.pictureUrl())
                .isActive(true)
                .roles(Set.of(roleRepository.findByRole(RoleType.USER).orElseThrow(ResourceNotFoundException::new)))
                .organization(organization)
                .build();
    }


    private UserProfileResponse buildUserProfileResponse() {
        return UserProfileResponse.builder()
                .userId(userInfo.getUser().getId())
                .fullName(userInfo.getUser().getFirstName() + " " + userInfo.getUser().getLastName())
                .organizationId(userInfo.getUser().getOrganization().getId())
                .pictureUrl(userInfo.getUser().getPictureUrl())
                .email(userInfo.getUser().getEmail())
                .build();
    }

    private UserProgressReport buildUserProgressReport(User user) {
        return new UserProgressReport(user.getId(), user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                new ArrayList<>(), new ArrayList<>(), 0, 0);
    }

    private JwtToken buildJwtToken(User user) {
        return jwtProvider.getJWTToken(user.getId(),
                user.getRoles().stream().map(Role::getRole).collect(Collectors.toList()),
                user.getOrganization().getId());
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

    public UserProgressReport getUserProgressByUserId(String userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found with this id:" + userId));

        UserProgressReport response = buildUserProgressReport(user);

        List<CourseAssignment> coursesAssignments = courseAssignmentRepository
                .findAllByOrganizationIdAndIsDeletedFalse(user.getOrganization().getId());

        for (CourseAssignment currentCourseAssignment : coursesAssignments) {

            Completion completion = completionRepository
                    .findByCourseIdAndUserIdAndIsDeletedFalse(currentCourseAssignment.getCourse().getId()
                            , user.getId()).orElse(null);

            if (completion == null) {
                List<Progress> progressForCurrentCourse = progressRepository
                        .findAllByUserIdAndCourseIdOrderByIsCompletedDescCreateDateAsc(user.getId()
                                , currentCourseAssignment.getCourse().getId());

                Integer rate = calculateRateOfProgress(progressForCurrentCourse);

                response.getNotCompletedCourses().add(buildCourseStatistics(currentCourseAssignment.getCourse()
                        , rate
                        , false));

                continue;
            }

            response.getCompletedCourses().add(buildCourseStatistics(currentCourseAssignment.getCourse()
                    , 100
                    , true));
        }

        return response;
    }

    private static CourseStatistics buildCourseStatistics(Course currentCourse, Integer rate, Boolean isCompleted) {
        return CourseStatistics.builder()
                .progressPercentage(rate)
                .courseTitle(currentCourse.getTitle())
                .courseTags(currentCourse.getTags())
                .coursePictureUrl(currentCourse.getPictureUrl())
                .completedDate(isCompleted ? new Date() : null)
                .isCompleted(isCompleted)
                .courseName(currentCourse.getName())
                .courseId(currentCourse.getId())
                .build();
    }
}
