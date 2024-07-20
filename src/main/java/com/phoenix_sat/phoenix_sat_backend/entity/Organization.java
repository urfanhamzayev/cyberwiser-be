package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.converter.OrganizationTypeConverter;
import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class Organization extends BaseEntity {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Type(value = OrganizationTypeConverter.class)
    private OrganizationType type;

     // TODO: Need to add
     // , description;
     // , phoneNumber;
     //, numEmployees;
     // country
    // email
    // industry
}
