package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.model.request.CreateOrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserProgressReport;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizationService {
    OrganizationResponse create(CreateOrganizationRequest createOrganizationRequest);

    void deleteUser(String userId);

    void importUsersFromFile(MultipartFile file);
}
