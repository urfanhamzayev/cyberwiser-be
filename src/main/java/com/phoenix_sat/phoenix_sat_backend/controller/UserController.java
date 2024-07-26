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
      // 1 ) User information for setting part (All detail,profile picture) +
      // 2)  User report . How many course completed, how many is progressing , and so on.+
     //  3)  Organization level user report, track report of all userr progress+
    //   4)  Batch import users and send invitation for user creation
     //  5)  Add one user for creation
     //  6)  Deactivate User once left the company +


    @GetMapping("/profile")
    public UserProfileResponse getUserProfile() {
        return userService.getUserProfile();
    }

    @GetMapping("/progress")
    public UserProgressReport getUserProgress() {
        return userService.getUserProgress();
    }

}
