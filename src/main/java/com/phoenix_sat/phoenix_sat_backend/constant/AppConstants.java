package com.phoenix_sat.phoenix_sat_backend.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Data
public class AppConstants {

    @Value("${app.organization.default-id}")
    private String defaultId;

}
