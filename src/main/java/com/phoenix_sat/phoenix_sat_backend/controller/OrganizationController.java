package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.request.CreateOrganizationRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.OrganizationResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserProgressReport;
import com.phoenix_sat.phoenix_sat_backend.service.OrganizationService;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;
    private final UserService userService;

    // TODO:
       //  1) :  Update Organization
       //  2) : Deactivate organization
      //  User can not login if it is deacitvate or organization level deactivated
    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public OrganizationResponse create(@RequestBody @Valid CreateOrganizationRequest createOrganizationRequest) {
        return organizationService.create(createOrganizationRequest);
    }

    @DeleteMapping("/{userId}/user")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public HttpStatus deleteUser(@PathVariable String userId) {
        organizationService.deleteUser(userId);
        return HttpStatus.OK;
    }

    @PostMapping(value = "/import", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public HttpStatus importUsers(@RequestPart MultipartFile file) {
        organizationService.importUsersFromFile(file);
        return HttpStatus.OK;
    }

    @GetMapping("/users-progress") // TODO :  users-statistics
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserProgressReport> getAllUserProgress() {
        return userService.getAllUserProgress();
    }
}


 // ##### PHOENIXMILD ### -> SuperAdmin (MAIN)
 // KapitalBank -> Admin  (SUB)
 // KapitalBank User -> user