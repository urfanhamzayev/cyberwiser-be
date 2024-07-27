package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.response.UserProfileResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserProgressReport;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import com.phoenix_sat.phoenix_sat_backend.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // TODO:
    //5)  Add one user for creation

    @GetMapping("/profile")
    public UserProfileResponse getUserProfile() {
        return userService.getUserProfile();
    }

    @GetMapping("/progress") // TODO:  statistics
    public UserProgressReport getUserProgress() {
        return userService.getUserProgress();
    }

}

// localhost:8080/{orgId}/{userId}

// localhost:8080/{orgId} -> azercell.phoenixmild.com/{userId}/{token} 1 week expire
                             // passs/comfirm