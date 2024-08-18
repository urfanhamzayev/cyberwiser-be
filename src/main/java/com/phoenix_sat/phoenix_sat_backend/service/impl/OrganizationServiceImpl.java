package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.entity.BaseEntity;
import com.phoenix_sat.phoenix_sat_backend.entity.Completion;
import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.error.exception.PermissionDeniedException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationUpdateRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserInfo;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import com.phoenix_sat.phoenix_sat_backend.service.FileService;
import com.phoenix_sat.phoenix_sat_backend.service.OrganizationService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final CompletionRepository completionRepository;
    private final CourseDetailRepository courseDetailRepository;
    private final ProgressRepository progressRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuizSectionRepository quizSectionRepository;
    private final LectureRepository lectureRepository;
    private final CourseContentRepository courseContentRepository;
    private final UserInfo userInfo;
    private final FileService fileService;
    private final JobLauncher jobLauncher;
    @Qualifier(value = "importUsersJob")
    private final Job userCsvImportJob;


    @Resource(name = "requestScopedUser")
    UserInfo currentUserInfo;


    @Override
    public OrganizationResponse create(OrganizationRequest organizationRequest) {
        Organization organization = organizationRepository.save(buildOrganization(organizationRequest));
        return buildOrganizationResponse(organization);
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found with this id:" + userId));

        if (!userInfo.getUser().getOrganization().getId().equals(user.getOrganization().getId()))
            throw new PermissionDeniedException("You do not have permission to perform this action.");

        userRepository.updateById(user.getId());
    }

    @SneakyThrows
    @Override
    public void importUsersFromFile(MultipartFile file) {
        log.info("User importing from csv file is starting... UserId: {} , OrgId: {}", userInfo.getUser().getId(), userInfo.getOrganization().getId());
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        fileService.saveFile(file.getBytes(), fileName);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", fileName)
                .addString("organizationId", currentUserInfo.getOrganization().getId())
                .toJobParameters();

        jobLauncher.run(userCsvImportJob, jobParameters);


    }

    @Override
    public OrganizationResponse update(OrganizationUpdateRequest organizationUpdateRequest) {
        log.info("Organization {} update process is starting.", userInfo.getUser().getId());

        Organization existingOrganization = organizationRepository.findById(userInfo.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        checkIsNotEmptyAndNullAndSetFieldExistingOrganization(organizationUpdateRequest, existingOrganization);

        Organization updatedOrganization = organizationRepository.save(existingOrganization);
        log.info("Organization {} updated successfully", userInfo.getOrganization().getId());
        return buildOrganizationResponse(updatedOrganization);

    }

    @Override
    public List<OrganizationResponse> getAllOrganization() {
        return organizationRepository.findAllByIsDeletedFalse()
                .stream()
                .map(OrganizationServiceImpl::buildOrganizationResponse)
                .toList();
    }

    @Transactional
    @Override
    public void deactivateOrganization(String organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with this id:" + organizationId));
        organization.setIsDeleted(true);

        setIsDeletedTrueInAllTablesByOrganizationId(organization);

        organizationRepository.save(organization);
    }

    private void setIsDeletedTrueInAllTablesByOrganizationId(Organization organization) {
        completionRepository.updateIsDeletedTrueByOrganizationId(true, organization.getId());
        courseContentRepository.updateIsDeletedByCourseId(true, organization.getId());
        quizRepository.updateIsDeletedByOrganizationId(true, organization.getId());
        lectureRepository.updateIsDeletedByOrganizationId(true, organization.getId());
        questionRepository.updateIsDeletedByOrganizationId(true, organization.getId());
        quizSectionRepository.updateIsDeletedByOrganizationId(true, organization.getId());
        progressRepository.updateIsDeletedByOrganizationId(true, organization.getId());
        courseDetailRepository.updateIsDeletedTrueByOrganizationId(true, organization.getId());
        courseAssignmentRepository.updateIsDeletedByOrganizationId(true, organization.getId());
        userRepository.updateUsersIsActiveByOrganizationId(false, true, organization.getId());
        courseRepository.updateCoursesIsActiveAndIsDeletedByOrganizationId(false, true, organization.getId());
    }


    private static void checkIsNotEmptyAndNullAndSetFieldExistingOrganization(OrganizationUpdateRequest organizationRequest,
                                                                              Organization existingOrganization) {
        if (organizationRequest.organizationName() != null && !organizationRequest.organizationName().isEmpty()) {
            existingOrganization.setName(organizationRequest.organizationName());
        }
        if (organizationRequest.email() != null && !organizationRequest.email().isEmpty()) {
            existingOrganization.setEmail(organizationRequest.email());
        }
        if (organizationRequest.description() != null && !organizationRequest.description().isEmpty()) {
            existingOrganization.setDescription(organizationRequest.description());
        }
        if (organizationRequest.phoneNumber() != null && !organizationRequest.phoneNumber().isEmpty()) {
            existingOrganization.setPhoneNumber(organizationRequest.phoneNumber());
        }
        if (organizationRequest.numEmployees() != null) {
            existingOrganization.setNumEmployees(organizationRequest.numEmployees());
        }
        if (organizationRequest.country() != null && !organizationRequest.country().isEmpty()) {
            existingOrganization.setCountry(organizationRequest.country());
        }
        if (organizationRequest.industry() != null && !organizationRequest.industry().isEmpty()) {
            existingOrganization.setIndustry(organizationRequest.industry());
        }
        if (organizationRequest.domain() != null && !organizationRequest.domain().isEmpty()) {
            existingOrganization.setDomain(organizationRequest.domain());
        }
    }

    private static OrganizationResponse buildOrganizationResponse(Organization organization) {
        return OrganizationResponse.builder().
                organizationId(organization.getId())
                .organizationName(organization.getName())
                .organizationType(organization.getType())
                .industry(organization.getIndustry())
                .email(organization.getEmail())
                .country(organization.getCountry())
                .domain(organization.getDomain())
                .description(organization.getDescription())
                .numEmployees(organization.getNumEmployees())
                .phoneNumber(organization.getPhoneNumber())
                .build();
    }

    private static Organization buildOrganization(OrganizationRequest organizationRequest) {
        return Organization.builder().type(organizationRequest.organizationType())
                .name(organizationRequest.organizationName())
                .build();
    }

    private List<? extends BaseEntity> setIsDeleted(Boolean isDeleted, List<? extends BaseEntity> objects) {
        return objects.stream().peek(baseEntity -> baseEntity.setIsDeleted(isDeleted)).collect(Collectors.toList());
    }

    private void setCompletionIsDeleted(Boolean isDeleted, List<Completion> completionList) {
        completionList.stream().peek(completion -> completion.setIsDeleted(isDeleted));
    }
}
