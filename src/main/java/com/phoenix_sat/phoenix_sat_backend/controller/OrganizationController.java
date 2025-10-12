package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.OrganizationUpdateRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserProgressReport;
import com.phoenix_sat.phoenix_sat_backend.service.OrganizationService;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;
    private final UserService userService;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public OrganizationResponse create(@ModelAttribute OrganizationRequest organizationRequest) {
        return organizationService.create(organizationRequest);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public OrganizationResponse update(@RequestBody OrganizationUpdateRequest organizationRequest) {
        return organizationService.update(organizationRequest);
    }

    @DeleteMapping("{organizationId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public HttpStatus deactivateOrganization(@PathVariable String organizationId) {
        organizationService.deactivateOrganization(organizationId);
        return HttpStatus.OK;
    }

    @DeleteMapping("/{userId}/user")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public HttpStatus deleteUser(@PathVariable String userId) {
        organizationService.deleteUser(userId);
        return HttpStatus.OK;
    }

    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public HttpStatus importUsers(@RequestPart MultipartFile file) {
        organizationService.importUsersFromFile(file);
        return HttpStatus.OK;
    }

    @GetMapping("/users-statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserProgressReport> getAllUserProgress() {
        return userService.getAllUserProgress();
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public List<OrganizationResponse> getAllOrganization() {
        return organizationService.getAllOrganization();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public OrganizationResponse getOrganization(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.startsWith("Bearer ") ?
                authorizationHeader.substring(7) : authorizationHeader;

        return organizationService.getOrganization(token);

    }
}