package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.CreateCourseRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.CourseResponse;
import com.phoenix_sat.phoenix_sat_backend.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @PostMapping
    public String create(){
        return organizationService.create();
    }

    @PostMapping("/course")
    public CourseResponse createCourse(@RequestBody CreateCourseRequest courseRequest){
        return organizationService.createCourse(courseRequest);
    }

}
