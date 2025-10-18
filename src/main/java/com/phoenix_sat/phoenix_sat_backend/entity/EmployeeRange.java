package com.phoenix_sat.phoenix_sat_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "employee_ranges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmployeeRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @Column(name = "min_employees")
    private Integer minEmployees;

    @Column(name = "max_employees")
    private Integer maxEmployees;

    @Column(name = "monthly_price")
    private BigDecimal monthlyPrice;

    private String currency;

    @Column(name = "is_active")
    private Boolean isActive;
}
