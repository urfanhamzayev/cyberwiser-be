package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.request.CreateOrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.service.impl.OrganizationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationServiceImpl organizationServiceImpl;

    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public OrganizationResponse create(@RequestBody @Valid CreateOrganizationRequest createOrganizationRequest) {
        return organizationServiceImpl.create(createOrganizationRequest);
    }
}
