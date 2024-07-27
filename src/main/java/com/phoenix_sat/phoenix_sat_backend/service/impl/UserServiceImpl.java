package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.constant.AppConstants;
import com.phoenix_sat.phoenix_sat_backend.entity.*;
import com.phoenix_sat.phoenix_sat_backend.error.exception.AuthenticationException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.jwt.JwtToken;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserLoginRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.*;
import com.phoenix_sat.phoenix_sat_backend.repository.CompletionRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.CourseAssignmentRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.ProgressRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import com.phoenix_sat.phoenix_sat_backend.security.JWTProvider;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final UserInfo userInfo;
    private final ProgressRepository progressRepository;
    private final CompletionRepository completionRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final AppConstants appConstants;

    @Override
    public JwtToken logIn(UserLoginRequest userLoginRequest) {
        logger.info("User login process starting...");
        User user = userRepository.findByEmailAndIsActiveTrue(userLoginRequest.email()).orElseThrow(() ->
                new ResourceNotFoundException("User not found with this email: " + userLoginRequest.email()));
        boolean isMatch = passwordEncoder.matches(userLoginRequest.password(), user.getPassword());
        if (isMatch) {
            logger.info("User logged successfully. UserId: {}",user.getId());
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

    private static CompletedCourse buildCompletedCourses(Course currentCourse, Completion completion) {
        return CompletedCourse.builder()
                .completedDate(completion.getCompletionDate())
                .courseId(currentCourse.getId())
                .courseName(currentCourse.getName())
                .coursePictureUrl(currentCourse.getPictureUrl())
                .courseTags(currentCourse.getTags())
                .courseTitle(currentCourse.getTitle())
                .build();
    }

    private InProgressCourses buildInProgressCourses(Course currentCourse, List<Progress> progressForCurrentCourse) {
        return InProgressCourses.builder()
                .courseId(currentCourse.getId())
                .courseName(currentCourse.getName())
                .coursePictureUrl(currentCourse.getPictureUrl())
                .progressPercentage(calculateRateOfProgress(progressForCurrentCourse))
                .courseTags(currentCourse.getTags())
                .courseTitle(currentCourse.getTitle())
                .build();
    }

    private UserProfileResponse buildUserProfileResponse() {
        return UserProfileResponse.builder()
                .userId(userInfo.getUser().getId())
                .name(userInfo.getUser().getName())
                .organizationId(userInfo.getUser().getOrganization().getId())
                .pictureUrl(userInfo.getUser().getPictureUrl())
                .email(userInfo.getUser().getEmail())
                .build();
    }

    private UserProgressReport buildUserProgressReport(User user) {
        return new UserProgressReport(user.getId(), user.getName(),
                user.getEmail(),
                new ArrayList<>(), new ArrayList<>());
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
        User user = userRepository.findByIdAndIsActiveTrue(userId).orElseThrow(()->
                new ResourceNotFoundException("User not found with this id:"+userId));

        UserProgressReport response = buildUserProgressReport(user);

        List<Course> coursesAssigned = courseAssignmentRepository
                .findAllByOrganizationIdAndMainOrganizationId(user.getOrganization().getId(), appConstants.getDefaultId());

        for (Course currentCourse : coursesAssigned) {

            Completion completion = completionRepository.findByCourseIdAndUserId(currentCourse.getId(), user.getId()).orElse(null);

            if (completion == null) {
                List<Progress> progressForCurrentCourse = progressRepository
                        .findAllByUserIdAndCourseIdOrderByCreateDate(user.getId(), currentCourse.getId());

                response.getInProgresses().add(buildInProgressCourses(currentCourse, progressForCurrentCourse));
                continue;
            }
            response.getCompleted().add(buildCompletedCourses(currentCourse, completion));
        }

        return response;
    }
}
