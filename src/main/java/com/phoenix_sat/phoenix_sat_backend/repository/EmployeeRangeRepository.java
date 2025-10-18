package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.EmployeeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRangeRepository extends JpaRepository<EmployeeRange, Long> {

    List<EmployeeRange> findAllByIsActiveTrueOrderByMinEmployeesAsc();
}