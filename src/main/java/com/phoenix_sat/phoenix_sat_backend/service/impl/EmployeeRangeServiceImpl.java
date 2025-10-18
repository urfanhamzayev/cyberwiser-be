package com.phoenix_sat.phoenix_sat_backend.service.impl;


import com.phoenix_sat.phoenix_sat_backend.entity.EmployeeRange;
import com.phoenix_sat.phoenix_sat_backend.model.request.EmployeeRangeRequest;
import com.phoenix_sat.phoenix_sat_backend.model.response.EmployeeRangeResponse;
import com.phoenix_sat.phoenix_sat_backend.repository.EmployeeRangeRepository;
import com.phoenix_sat.phoenix_sat_backend.service.EmployeeRangeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeRangeServiceImpl implements EmployeeRangeService {

    private final EmployeeRangeRepository employeeRangeRepository;

    // ✅ Get all active ranges
    public List<EmployeeRangeResponse> getActiveRanges() {
        return employeeRangeRepository.findAllByIsActiveTrueOrderByMinEmployeesAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get range by ID
    public EmployeeRangeResponse getRangeById(Long id) {
        EmployeeRange range = employeeRangeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee range not found"));
        return mapToResponse(range);
    }

    // ✅ Create a new range
    public EmployeeRangeResponse createRange(EmployeeRangeRequest request) {
        EmployeeRange range = EmployeeRange.builder()
                .label(request.getLabel())
                .minEmployees(request.getMinEmployees())
                .maxEmployees(request.getMaxEmployees())
                .monthlyPrice(request.getMonthlyPrice())
                .currency(request.getCurrency())
                .isActive(request.getIsActive())
                .build();

        return mapToResponse(employeeRangeRepository.save(range));
    }

//    // ✅ Update existing range
//    public EmployeeRangeResponse updateRange(Long id, EmployeeRangeRequest request) {
//        EmployeeRange existing = employeeRangeRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Employee range not found"));
//
//        existing.setLabel(request.getLabel());
//        existing.setMinEmployees(request.getMinEmployees());
//        existing.setMaxEmployees(request.getMaxEmployees());
//        existing.setMonthlyPrice(request.getMonthlyPrice());
//        existing.setCurrency(request.getCurrency());
//        existing.setIsActive(request.getIsActive());
//
//        return mapToResponse(employeeRangeRepository.save(existing));
//    }
//
//    // ✅ Soft delete / deactivate
//    public void deactivateRange(Long id) {
//        EmployeeRange range = employeeRangeRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Employee range not found"));
//        range.setIsActive(false);
//        employeeRangeRepository.save(range);
//    }

    // Mapper helper
    private EmployeeRangeResponse mapToResponse(EmployeeRange range) {
        return EmployeeRangeResponse.builder()
                .id(range.getId())
                .label(range.getLabel())
                .minEmployees(range.getMinEmployees())
                .maxEmployees(range.getMaxEmployees())
                .monthlyPrice(range.getMonthlyPrice())
                .currency(range.getCurrency())
                .isActive(range.getIsActive())
                .build();
    }
}
