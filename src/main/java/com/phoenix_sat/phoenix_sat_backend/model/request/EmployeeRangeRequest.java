package com.phoenix_sat.phoenix_sat_backend.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeRangeRequest {
    private String label;
    private Integer minEmployees;
    private Integer maxEmployees;
    private BigDecimal monthlyPrice;
    private String currency;
    private Boolean isActive = true;
}
