package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.error.exception.PermissionDeniedException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.request.CreateOrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserInfo;
import com.phoenix_sat.phoenix_sat_backend.repository.OrganizationRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import com.phoenix_sat.phoenix_sat_backend.service.FileService;
import com.phoenix_sat.phoenix_sat_backend.service.OrganizationService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final UserInfo userInfo;
    private final FileService fileService;
    private final JobLauncher jobLauncher;
    private final Job userCsvImportJob;

    @Resource(name = "requestScopedUser")
    UserInfo currentUserInfo;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                   UserRepository userRepository,
                                   UserInfo userInfo, FileService fileService,
                                   JobLauncher jobLauncher,
                                   @Qualifier(value = "importUsersJob")  Job userCsvImportJob) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.userInfo = userInfo;
        this.fileService = fileService;
        this.jobLauncher = jobLauncher;
        this.userCsvImportJob = userCsvImportJob;
    }

    @Override
    public OrganizationResponse create(CreateOrganizationRequest createOrganizationRequest) {
        Organization organization = organizationRepository.save(buildOrganization(createOrganizationRequest));
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
//        try {
//            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
//            fileService.saveFile(file.getBytes(), fileName);
//
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("filename", Objects.requireNonNull(fileName))
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//
//
//            jobLauncher.run(importUserJob, jobParameters);
//            return "File uploaded and processing started!";
//        } catch (Exception e) {
//            // Log the full stack trace
//            e.printStackTrace();
//            return "Failed to process file.";
//        }
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        fileService.saveFile(file.getBytes(), fileName);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", fileName)
                .addString("organizationId", currentUserInfo.getOrganization().getId())
                .toJobParameters();

        jobLauncher.run(userCsvImportJob, jobParameters);
    }

    private static OrganizationResponse buildOrganizationResponse(Organization organization) {
        return OrganizationResponse.builder().
                organizationId(organization.getId())
                .organizationName(organization.getName())
                .build();
    }

    private static Organization buildOrganization(CreateOrganizationRequest createOrganizationRequest) {
        return Organization.builder().type(createOrganizationRequest.organizationType())
                .name(createOrganizationRequest.organizationName())
                .build();
    }
}
