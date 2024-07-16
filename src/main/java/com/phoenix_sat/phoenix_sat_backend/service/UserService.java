package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.model.jwt.JwtToken;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserLoginRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.CourseContentResponse;

public interface UserService {
    JwtToken logIn(UserLoginRequest userLoginRequest);
}
