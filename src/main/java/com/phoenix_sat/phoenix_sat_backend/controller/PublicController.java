package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.jwt.JwtToken;
import com.phoenix_sat.phoenix_sat_backend.model.request.RegistrationCompletionRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserLoginRequest;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserRegisterRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserRegisterResponse;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/public")
public class PublicController {
    private final UserService userService;

    @PostMapping("/logIn")
    public JwtToken logIn(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        return userService.logIn(userLoginRequest);
    }
}
