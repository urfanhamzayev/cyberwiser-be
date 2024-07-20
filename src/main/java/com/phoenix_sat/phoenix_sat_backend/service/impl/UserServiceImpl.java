package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.phoenix_sat.phoenix_sat_backend.entity.Role;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.error.exception.AuthenticationException;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.jwt.JwtToken;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserLoginRequest;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import com.phoenix_sat.phoenix_sat_backend.security.JWTProvider;
import com.phoenix_sat.phoenix_sat_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;

    @Override
    public JwtToken logIn(UserLoginRequest userLoginRequest) {
         // TODO:
           // Need to check user is active
        User user = userRepository.findByEmail(userLoginRequest.email()).orElseThrow(() ->
                new ResourceNotFoundException("User not found with this email: " + userLoginRequest.email()));
        boolean isMatch = passwordEncoder.matches(userLoginRequest.password(), user.getPassword());
        if (isMatch)
            return buildJwtToken(user);

        throw new AuthenticationException("Bad credentials");
    }

    private JwtToken buildJwtToken(User user) {
        return jwtProvider.getJWTToken(user.getId(),
                user.getRoles().stream().map(Role::getRole).collect(Collectors.toList()),
                user.getOrganization().getId());
    }
}
