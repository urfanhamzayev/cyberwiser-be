package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.model.request.CreateOrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;

public interface OrganizationService {
    OrganizationResponse create(CreateOrganizationRequest createOrganizationRequest);
}
