package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.model.request.CreateOrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final CourseRepository courseRepository;
    private final CourseContentRepository courseContentRepository;
    private final LectureRepository lectureRepository;
    private final QuizRepository quizRepository;

    public OrganizationResponse create(CreateOrganizationRequest createOrganizationRequest) {
        Organization organization = organizationRepository.save(buildOrganization(createOrganizationRequest));
        return buildOrganizationResponse(organization);
    }

    private static OrganizationResponse buildOrganizationResponse(Organization organization) {
        return OrganizationResponse.builder().organizationId(organization.getId())
                .organizationName(organization.getName())
                .build();
    }

    private static Organization buildOrganization(CreateOrganizationRequest createOrganizationRequest) {
        return Organization.builder().type(createOrganizationRequest.organizationType())
                .name(createOrganizationRequest.organizationName())
                .build();
    }
}
