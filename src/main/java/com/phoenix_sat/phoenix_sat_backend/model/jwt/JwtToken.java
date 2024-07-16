package com.phoenix_sat.phoenix_sat_backend.model.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtToken {
    private String token;
    private Long createDate;
    private Long expirationDate;
}
