package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.model.jwt.JwtToken;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserLoginRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserRegisterRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.CourseContentResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserProfileResponse;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserProgressReport;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserRegisterResponse;

import java.util.List;

public interface UserService {
    JwtToken logIn(UserLoginRequest userLoginRequest);

    UserProfileResponse getUserProfile();

    UserProgressReport getUserProgress();

    List<UserProgressReport> getAllUserProgress();

    UserRegisterResponse register(String organizationId, UserRegisterRequest userRegisterRequest);
}
