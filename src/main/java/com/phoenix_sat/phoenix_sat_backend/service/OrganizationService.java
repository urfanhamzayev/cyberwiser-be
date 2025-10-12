package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationUpdateRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizationService {
    OrganizationResponse create(OrganizationRequest organizationRequest);

    void deleteUser(String userId);

    void importUsersFromFile(MultipartFile file);

    void deactivateOrganization(String organizationId);

    OrganizationResponse update(OrganizationUpdateRequest organizationUpdateRequest);

    List<OrganizationResponse> getAllOrganization();

    OrganizationResponse getOrganization(String token);
}
