package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.converter.OrganizationTypeConverter;
import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.enums.PricingType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("is_deleted = false")
public class Organization extends BaseEntity {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    @Type(value = OrganizationTypeConverter.class)
    private OrganizationType type;

    private String description;

    @Column(name = "phone_number")
    private String phoneNumber;

//    @Column(name = "num_employees")
//    private Integer numEmployees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", referencedColumnName = "code")
    private Country country;
//    private String country;

    private String industry;

    private String domain;

    @Column(name = "logo_key_name")
    private String logoKeyName;

    // 🔹 NEW COLUMNS
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_range_id")
    private EmployeeRange employeeRange;

    @Column(name = "custom_monthly_price")
    private BigDecimal customMonthlyPrice;

    @Column(name = "custom_currency")
    private String customCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false)
    private PricingType pricingType = PricingType.DEFAULT;
}
