package com.phoenix_sat.phoenix_sat_backend.controller;


import com.phoenix_sat.phoenix_sat_backend.model.response.EmployeeRangeResponse;
import com.phoenix_sat.phoenix_sat_backend.service.EmployeeRangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee-ranges")
@RequiredArgsConstructor
public class EmployeeRangeController {

    private final EmployeeRangeService employeeRangeService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<List<EmployeeRangeResponse>> getAll() {
        return ResponseEntity.ok(employeeRangeService.getActiveRanges());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<EmployeeRangeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeRangeService.getRangeById(id));
    }

//    @PostMapping
//    public ResponseEntity<EmployeeRangeResponse> create(@RequestBody EmployeeRangeRequest request) {
//        return ResponseEntity.ok(employeeRangeService.createRange(request));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<EmployeeRangeResponse> update(
//            @PathVariable Long id, @RequestBody EmployeeRangeRequest request) {
//        return ResponseEntity.ok(employeeRangeService.updateRange(id, request));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
//        employeeRangeService.deactivateRange(id);
//        return ResponseEntity.noContent().build();
//    }
}

