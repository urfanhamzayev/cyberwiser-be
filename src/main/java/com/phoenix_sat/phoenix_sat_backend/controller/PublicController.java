package com.phoenix_sat.phoenix_sat_backend.controller;

import com.phoenix_sat.phoenix_sat_backend.model.jwt.JwtToken;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserLoginRequest;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
