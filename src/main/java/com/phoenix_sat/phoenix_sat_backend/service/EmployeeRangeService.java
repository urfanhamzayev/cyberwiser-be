package com.phoenix_sat.phoenix_sat_backend.service;

import com.phoenix_sat.phoenix_sat_backend.model.response.EmployeeRangeResponse;

import java.util.List;

public interface EmployeeRangeService  {
    List<EmployeeRangeResponse> getActiveRanges();

    EmployeeRangeResponse getRangeById(Long id);
}
