package com.phoenix_sat.phoenix_sat_backend.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmployeeRangeResponse {
    private Long id;
    private String label;
    private Integer minEmployees;
    private Integer maxEmployees;
    private BigDecimal monthlyPrice;
    private String currency;
    private Boolean isActive;
}
