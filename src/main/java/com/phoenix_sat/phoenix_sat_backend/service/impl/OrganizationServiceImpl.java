package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.config.CustomEventPublisher;
import com.phoenix_sat.phoenix_sat_backend.entity.Country;
import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.entity.Role;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import com.phoenix_sat.phoenix_sat_backend.error.exception.PermissionDeniedException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.event.RegistrationVerificationEvent;
import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationUpdateRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserInfo;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import com.phoenix_sat.phoenix_sat_backend.security.JWTProvider;
import com.phoenix_sat.phoenix_sat_backend.service.OrganizationService;
import com.phoenix_sat.phoenix_sat_backend.service.S3Service;
import com.phoenix_sat.phoenix_sat_backend.util.UserUtil;
import com.phoenix_sat.phoenix_sat_backend.util.Util;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private final JobLauncher jobLauncher;
    @Qualifier(value = "importUsersJob")
    private final Job userCsvImportJob;
    @Resource(name = "requestScopedUser")
    UserInfo currentUserInfo;
    private final S3ServiceImpl s3ServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final CustomEventPublisher eventPublisher;
    private final S3Service s3Service;
    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final JWTProvider jwtProvider;


    @Override
    public OrganizationResponse create(@RequestBody OrganizationRequest organizationRequest) {
        if (!isSuperAdmin())
            throw new PermissionDeniedException("You don't have permission to create organization. UserId: "
                                                +userInfo.getUser().getId());

        Country country = countryRepository.findByCode(organizationRequest.countryCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid country code: " + organizationRequest.countryCode()));

        Organization organization = organizationRepository.save(buildOrganization(organizationRequest,country));

        String keyNameForLogo = organization.getId() + organizationRequest.logo().getOriginalFilename() + Util.generateRandomUUID();
        organization.setLogoKeyName(keyNameForLogo);
        organizationRepository.save(organization);

        s3Service.uploadFile(keyNameForLogo, organizationRequest.logo());

        String temporaryPassword = UserUtil.getTemporaryPasswordForUser();

        User user = buildUser(organizationRequest,organization, temporaryPassword);

        userRepository.save(user);

        eventPublisher.publish(new RegistrationVerificationEvent(List.of(user),List.of(temporaryPassword)));

        return buildOrganizationResponse(organization);
    }

    private User buildUser(OrganizationRequest organizationRequest,Organization organization, String temporaryPassword) {

        Role adminRole = roleRepository.findByRole(RoleType.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found. Seed roles first."));

        return User.builder()
                .email(organizationRequest.adminEmail())
                .firstName(getUserFirstNameByFullname(organizationRequest.adminFullname()))
                .lastName(getUserLastNameByFullname(organizationRequest.adminFullname()))
                .password(passwordEncoder.encode(temporaryPassword))
                .organization(organization)
                .roles(Set.of(adminRole))
                .build();
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found with this id:" + userId));

        if (!userInfo.getUser().getOrganization().getId().equals(user.getOrganization().getId()))
            throw new PermissionDeniedException("You do not have permission to perform this action.");

        userRepository.updateIsActiveAndIsDeletedById(user.getId(), false, true);
    }

    @SneakyThrows
    @Override
    public void importUsersFromFile(MultipartFile file) {
        log.info("User importing from csv file is starting... UserId: {} , OrgId: {}", userInfo.getUser().getId(), userInfo.getOrganization().getId());
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
//        fileService.saveFile(file.getBytes(), fileName);
        s3ServiceImpl.uploadFile(fileName, file);
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

        Country country = countryRepository.findByCode(organizationUpdateRequest.countryCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid country code: " + organizationUpdateRequest.countryCode()));

        updateOrganization(organizationUpdateRequest, existingOrganization);

        Organization updatedOrganization = organizationRepository.save(existingOrganization);
        log.info("Organization {} updated successfully", userInfo.getOrganization().toString());
        return buildOrganizationResponse(updatedOrganization);

    }

    @Override
    public List<OrganizationResponse> getAllOrganization() {
        return organizationRepository.findAllByIsDeletedFalse()
                .stream()
                .map(OrganizationServiceImpl::buildOrganizationResponse)
                .toList();
    }

    @Override
    public OrganizationResponse getOrganization(String token) {
        // 1️⃣ Get organizationId from JWT
        String organizationId = jwtProvider.getOrganizationIdFromToken(token);


        // 2️⃣ Find organization by ID (and not deleted)
        Organization organization = organizationRepository.findByIdAndIsDeletedFalse(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + organizationId));

        // 3️⃣ Build response
        return buildOrganizationResponse(organization);
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


    private boolean isSuperAdmin() {
        for (Role role : userInfo.getUser().getRoles()) {
            if (role.getRole() == RoleType.SUPER_ADMIN)
                return true;
        }
        return false;
    }

    private String getUserFirstNameByFullname(String fullname) {
        return fullname.split(" ")[0];
    }

    private static void updateOrganization(OrganizationUpdateRequest organizationUpdateRequest, Organization existingOrganization) {
//        existingOrganization.setCountry(organizationUpdateRequest.country()); //todo @Sarkhan
        existingOrganization.setName(organizationUpdateRequest.organizationName());
        existingOrganization.setDescription(organizationUpdateRequest.description());
        existingOrganization.setEmail(organizationUpdateRequest.email());
        existingOrganization.setIndustry(organizationUpdateRequest.industry());
        existingOrganization.setNumEmployees(organizationUpdateRequest.numEmployees());
        existingOrganization.setPhoneNumber(organizationUpdateRequest.phoneNumber());
    }

    private String getUserLastNameByFullname(String fullname) {
        return fullname.split(" ")[1];
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




    private static OrganizationResponse buildOrganizationResponse(Organization organization ) {
        return OrganizationResponse.builder().
                organizationId(organization.getId())
                .organizationName(organization.getName())
                .organizationType(organization.getType())
                .industry(organization.getIndustry())
                .email(organization.getEmail())
                .countryCode(organization.getCountry().getCode())
                .domain(organization.getDomain())
                .description(organization.getDescription())
                .numEmployees(organization.getNumEmployees())
                .phoneNumber(organization.getPhoneNumber())
                .logoKeyName(organization.getLogoKeyName())
                .build();
    }

    private static Organization buildOrganization(OrganizationRequest organizationRequest, Country country) {
        return Organization.builder().type(organizationRequest.organizationType())
                .name(organizationRequest.organizationName())
                .type(organizationRequest.organizationType())
                .email(organizationRequest.email())
                .domain(organizationRequest.domain())
                .industry(organizationRequest.industry())
                .country(country)
                .description(organizationRequest.description())
                .numEmployees(organizationRequest.numEmployees())
                .phoneNumber(organizationRequest.phoneNumber())
                .build();
    }

    public static String generateRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
