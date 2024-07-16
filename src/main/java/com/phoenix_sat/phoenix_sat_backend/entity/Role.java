package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.converter.RoleTypeConverter;
import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Role {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    @Enumerated(EnumType.STRING)
    @Type(value = RoleTypeConverter.class)
    private RoleType role;
}
