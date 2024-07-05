package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.model.CreateCourseRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.CourseResponse;
import com.phoenix_sat.phoenix_sat_backend.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    public String create() {
        return null;

    }

    public CourseResponse createCourse(CreateCourseRequest courseRequest) {
    return null;
    }
}
