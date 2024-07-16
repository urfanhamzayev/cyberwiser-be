package com.phoenix_sat.phoenix_sat_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(8);
    }
}
