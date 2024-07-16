package com.phoenix_sat.phoenix_sat_backend.config;

import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.response.UserInfo;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@RequiredArgsConstructor
public class RequestScopedUserProvider {

    private final UserRepository userRepository;

    @RequestScope
    @Bean
    public UserInfo requestScopedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
            return null;

        String userId = authentication.getName();

        if (StringUtils.isEmpty(userId))
            return null;

        var user =  userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);
        return UserInfo.builder()
                .user(user)
                .build();
    }
}
